package com.orderengine.order.service.impl;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.order.domain.*;
import com.orderengine.order.repository.InvoiceRepository;
import com.orderengine.order.repository.OrderEventRepository;
import com.orderengine.order.repository.OrderRepository;
import com.orderengine.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String DEFAULT_CURRENCY = "INR";
    private static final String STUB_PDF_URL_TEMPLATE = "https://storage.local/invoices/%s.pdf";

    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;
    private final InvoiceRepository invoiceRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderEventRepository orderEventRepository, InvoiceRepository invoiceRepository) {
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public OrderEntity createOrder(
            String customerId,
            List<NewOrderItem> items,
            ShippingAddress shippingAddress,
            String currency
    ) {
        BigDecimal total = items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setOrderId(UUID.randomUUID().toString());
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(total);
        order.setCurrency(currency == null || currency.isBlank() ? DEFAULT_CURRENCY : currency);
        order.setShippingAddress(shippingAddress);

        for(NewOrderItem item : items) {
            OrderItemEntity line = new OrderItemEntity();
            line.setSku(item.sku());
            line.setQuantity(item.quantity());
            line.setUnitPrice(item.unitPrice());
            order.addItem(line);
        }

        OrderEntity saved = orderRepository.save(order);
        appendEvent(saved.getOrderId(), "order.created", Map.of(
                "status", saved.getStatus().name(),
                "customerId", saved.getCustomerId(),
                "totalAmount", saved.getTotalAmount()
        ));
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderEngineException(ErrorCode.NOT_FOUND, "Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderEntity> listOrders(OrderStatus status, Pageable pageable) {
        if(status == null){
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
    }

    private void appendEvent(String orderId, String eventType, Map<String, Object> payload) {
        OrderEventEntity event = new OrderEventEntity();
        event.setOrderId(orderId);
        event.setEventType(eventType);
        event.setPayload(payload);
        orderEventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public ValidationResult validateOrder(String orderId) {
        OrderEntity order = getOrder(orderId);
        List<String> errors = new ArrayList<>();

        boolean validItems = validateItems(order.getItems(), errors);
        boolean validAddress = validateAddress(order.getShippingAddress(), errors);

        return new ValidationResult(validItems && validAddress, validItems, validAddress, List.copyOf(errors));
    }

    private boolean validateAddress(ShippingAddress address, List<String> errors) {
        if(address == null) {
            errors.add("shippinAddress is required");
            return false;
        }
        boolean ok = true;
        if(isBlank(address.line1())){
            errors.add("shippingAddress.line1 must not be blank");
            ok = false;
        }
        if(isBlank(address.city())){
            errors.add("shippingAddress.city must not be blank");
            ok = false;
        }
        if(isBlank(address.state())){
            errors.add("shippingAddress.state must not be blank");
            ok = false;
        }
        if(isBlank(address.postalCode())){
            errors.add("shippingAddress.postalCode must not be blank");
            ok = false;
        }
        if(isBlank(address.country())){
            errors.add("shippingAddress.country must not be blank");
            ok = false;
        }
        return ok;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean validateItems(List<OrderItemEntity> items, List<String> errors) {
        if(items == null || items.isEmpty()) {
            errors.add("Order must contain at least one item");
        }

        boolean ok = true;
        for(int i = 0; i < items.size(); i++) {
            OrderItemEntity item = items.get(i);
            String prefix = "items[" + i + "]";
            if(item.getSku() == null || item.getSku().isBlank()) {
                errors.add(prefix + ".sku must not be blank");
                ok = false;
            }
            if(item.getQuantity() <= 0) {
                errors.add(prefix + ".quantity must be greater than 0");
                ok = false;
            }
            if(item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0){
                errors.add(prefix + ".unitPrice must be greater than 0");
                ok = false;
            }
        }
        return ok;
    }

    @Override
    @Transactional
    public InvoiceEntity generateInvoice(String orderId, String paymentId) {
        if(paymentId == null || paymentId.isBlank()) {
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "paymentId must not be blank");
        }

        OrderEntity order = getOrder(orderId);

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceId(UUID.randomUUID().toString());
        invoice.setPaymentId(paymentId.trim());
        invoice.setOrderId(order.getOrderId());
        invoice.setAmount(order.getTotalAmount());
        invoice.setPdfUrl(STUB_PDF_URL_TEMPLATE.formatted(invoice.getInvoiceId()));

        InvoiceEntity saved = invoiceRepository.save(invoice);
        appendEvent(order.getOrderId(), "order.invoice.generated", Map.of(
                "invoiceId", saved.getInvoiceId(),
                "paymentId", saved.getPaymentId(),
                "amount", saved.getAmount()
        ));
        return saved;
    }

    @Override
    @Transactional
    public OrderEntity updateStatus(String orderId, OrderStatus status, String reason) {
        if(status == null){
            throw new OrderEngineException(ErrorCode.BAD_REQUEST, "status must not be null");
        }

        OrderEntity order = getOrder(orderId);
        OrderStatus previous = order.getStatus();
        order.setStatus(status);
        OrderEntity saved = orderRepository.save(order);

        if(previous == status) {
            return order;
        }

        if(!OrderStatusTransitions.isAllowed(previous, status)) {
            throw new OrderEngineException(
                    ErrorCode.CONFLICT,
                    "Illegal status transition: " + previous + " -> " + status + "; allowed="
                    + OrderStatusTransitions.allowedFrom(previous)
            );
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", previous.name());
        payload.put("to", saved.getStatus().name());
        if(reason != null && !reason.isBlank()) {
            payload.put("reason", reason.trim());
        }
        appendEvent(saved.getOrderId(), "order.status.changed", payload);
        return saved;
    }

}
