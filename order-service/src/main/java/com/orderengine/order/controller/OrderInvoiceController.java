package com.orderengine.order.controller;

import com.orderengine.common.error.ErrorCode;
import com.orderengine.common.error.OrderEngineException;
import com.orderengine.order.api.dto.InvoiceRequest;
import com.orderengine.order.api.dto.InvoiceResponse;
import com.orderengine.order.domain.InvoiceEntity;
import com.orderengine.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Create, get and list orders")
public class OrderInvoiceController {

    private final OrderService orderService;

    public OrderInvoiceController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/invoice")
    @Operation(summary = "Generate invoice", description = "Generates an invoice row, pdfUrl is a stub until storage exists")
    public ResponseEntity<InvoiceResponse> generateInvoice(
            @PathVariable String orderId,
            @Valid @RequestBody InvoiceRequest request
    ) {
        if(request.orderId() != null &&  !request.orderId().isBlank() && !request.orderId().equals(orderId)) {
            throw new OrderEngineException(
                    ErrorCode.BAD_REQUEST,
                    "orderId inn body must match path orderId"
            );
        }

        log.info("generateInvoice orderId={} paymentId={}", orderId, request.paymentId());
        InvoiceEntity invoice = orderService.generateInvoice(orderId, request.paymentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(invoice));
    }

    private static InvoiceResponse toResponse(InvoiceEntity invoice) {
        return new InvoiceResponse(
                invoice.getInvoiceId(),
                invoice.getOrderId(),
                invoice.getPdfUrl(),
                invoice.getAmount(),
                invoice.getIssuedAt()
        );
    }
}
