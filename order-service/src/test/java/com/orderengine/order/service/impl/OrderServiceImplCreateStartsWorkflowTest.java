package com.orderengine.order.service.impl;

import com.orderengine.order.client.orchestration.OrchestrationClient;
import com.orderengine.order.client.orchestration.WorkflowInstanceResponse;
import com.orderengine.order.domain.OrderEntity;
import com.orderengine.order.domain.OrderEventEntity;
import com.orderengine.order.domain.ShippingAddress;
import com.orderengine.order.repository.InvoiceRepository;
import com.orderengine.order.repository.OrderEventRepository;
import com.orderengine.order.repository.OrderRepository;
import com.orderengine.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplCreateStartsWorkflowTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventRepository orderEventRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrchestrationClient orchestrationClient;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        PlatformTransactionManager transactionManager =
                new PlatformTransactionManager() {
                    @Override
                    public TransactionStatus getTransaction(
                            TransactionDefinition definition) {
                        return new SimpleTransactionStatus();
                    }

                    @Override
                    public void commit(TransactionStatus status) {
                    }

                    @Override
                    public void rollback(TransactionStatus status) {
                    }
                };

        orderService = new OrderServiceImpl(
                orderRepository,
                orderEventRepository,
                invoiceRepository,
                orchestrationClient,
                transactionManager
        );

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderEventRepository.save(any(OrderEventEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createOrderStartsWorkflowAndStoresInstanceKey() {
        String instanceKey = "2251799813685249";

        when(orchestrationClient.startOrderFulfillment(any(UUID.class)))
                .thenAnswer(invocation -> {
                    UUID orderId = invocation.getArgument(0);

                    return new WorkflowInstanceResponse(
                            instanceKey,
                            "process_order_fulfillment",
                            1,
                            orderId,
                            "ACTIVE",
                            Instant.parse("2026-10-05T12:00:00Z")
                    );
                });

        OrderEntity created = orderService.createOrder(
                "cust-1",
                List.of(
                        new OrderService.NewOrderItem(
                                "SKU-1",
                                1,
                                new BigDecimal("10.00")
                        )
                ),
                new ShippingAddress(
                        "1 Main",
                        null,
                        "Pune",
                        "MH",
                        "411001",
                        "IN"
                ),
                "INR"
        );

        assertThat(created.getWorkflowKey()).isEqualTo(instanceKey);

        verify(orchestrationClient).startOrderFulfillment(
                UUID.fromString(created.getOrderId())
        );

        ArgumentCaptor<OrderEntity> saved =
                ArgumentCaptor.forClass(OrderEntity.class);

        verify(orderRepository, org.mockito.Mockito.atLeast(2))
                .save(saved.capture());

        assertThat(saved.getAllValues().getLast().getWorkflowKey())
                .isEqualTo(instanceKey);
    }
}
