package com.orderengine.common.orchestration;

public class HandlerActivityName {

    public static final String PROCESS_ORDER_FULLFILLMENT = "process_order_fullfillment";

    //order intake and validation
    public static final String GET_ORDER_FOR_FULFILLMENT = "getOrderForFulfillment";
    public static final String VALIDATE_ORDER_ITMES = "validateOrderItems";
    public static final String VALIDATE_SHIPPING_ORDER = "ValidateShippingOrder";
    public static final String GATEWAY_VALIDATION_RESULT = "GatewayValidationResult";
    public static final String UPDATE_ORDER_STATUS_TO_VALIDATED = "UpdateOrderStatusToValidated";
    public static final String UPDATE_ORDER_STATUS_TO_REJECTED = "UpdateOrderStatusToRejected";

    //Parallel fulfillment fork
    public static final String GATEWAY_PARALLEL_FORK = "GatewayParallelFork";
    public static final String BUILD_REVERSE_INVENTORY_REQUEST = "BuildReverseInventoryRequest";
    public static final String REVERSE_INVENTORY_IN_INVENTORY_SERVICE = "ReverseInventoryInInventoryService";
    public static final String EXTRACT_RESERVATION_ID = "ExtractReservationId";
    public static final String BUILD_AUTHORIZE_PAYMENT_REQUEST = "BuildAuthorizePaymentRequest";
    public static final String AUTHORIZE_PAYMENT_IN_PAYMENT_SERVICE = "AuthorizePaymentInPaymentService";
    public static final String EXTRACT_PAYMENT_ID = "ExtractPaymentId";
    public static final String BUILD_FRAUD_ASSESSMENT_REQUEST = "BuildFraudAssessmentRequest";
    public static final String ASSES_FRAUD_IN_FRAUD_SERVICE = "AssesFraudInFraudService";
    public static final String EXTRACT_FRAUD_SCORE = "ExtractFraudScore";
    public static final String GATEWAY_PARALLEL_JOIN = "GatewayParallelJoin";

    // ==========================================================
// §4.3 Fraud Review Activities
// ==========================================================

    public static final String GATEWAY_FRAUD_REVIEW_REQUIRED = "GatewayFraudReviewRequired";
    public static final String UPDATE_ORDER_STATUS_TO_FRAUD_REVIEW = "UpdateOrderStatusToFraudReview";
    public static final String FRAUD_MANAGER_REVIEW = "FraudManagerReview";
    public static final String RECORD_FRAUD_REVIEW_DECISION = "RecordFraudReviewDecision";
    public static final String GATEWAY_FRAUD_APPROVED = "GatewayFraudApproved";


// ==========================================================
// §4.4 Payment Capture & Shipping Activities
// ==========================================================

    public static final String CAPTURE_PAYMENT_IN_PAYMENT_SERVICE = "CapturePaymentInPaymentService";
    public static final String UPDATE_ORDER_STATUS_TO_PAID = "UpdateOrderStatusToPaid";
    public static final String BUILD_CREATE_SHIPMENT_REQUEST = "BuildCreateShipmentRequest";
    public static final String CREATE_SHIPMENT_IN_SHIPPING_SERVICE = "CreateShipmentInShippingService";
    public static final String EXTRACT_SHIPMENT_ID = "ExtractShipmentId";
    public static final String UPDATE_ORDER_STATUS_TO_SHIPPED = "UpdateOrderStatusToShipped";


// ==========================================================
// §4.5 Notification & Invoice Activities
// ==========================================================

    public static final String BUILD_ORDER_SHIPPED_NOTIFICATION_REQUEST = "BuildOrderShippedNotificationRequest";
    public static final String SEND_ORDER_SHIPPED_NOTIFICATION = "SendOrderShippedNotification";
    public static final String GENERATE_INVOICE_IN_ORDER_SERVICE = "GenerateInvoiceInOrderService";
    public static final String UPDATE_ORDER_STATUS_TO_COMPLETED = "UpdateOrderStatusToCompleted";

// ==========================================================
// §4.6 Payment Timeout Activities
// ==========================================================

    public static final String TIMER_PAYMENT_TIMEOUT = "TimerPaymentTimeout";
    public static final String UPDATE_ORDER_STATUS_TO_PAYMENT_TIMEOUT = "UpdateOrderStatusToPaymentTimeout";

// ==========================================================
// §4.7 Compensation / Cancellation Activities
// ==========================================================

    public static final String GATEWAY_COMPENSATION_ENTRY = "Gateway_CompensationEntry";
    public static final String VOID_PAYMENT_IN_PAYMENT_SERVICE = "VoidPaymentInPaymentService";
    public static final String REFUND_PAYMENT_IN_PAYMENT_SERVICE = "RefundPaymentInPaymentService";
    public static final String GATEWAY_PAYMENT_COMPENSATION_TYPE = "Gateway_PaymentCompensationType";
    public static final String CANCEL_SHIPMENT_IN_SHIPPING_SERVICE = "CancelShipmentInShippingService";
    public static final String RELEASE_INVENTORY_IN_INVENTORY_SERVICE = "ReleaseInventoryInInventoryService";
    public static final String BUILD_ORDER_CANCELLED_NOTIFICATION_REQUEST = "BuildOrderCancelledNotificationRequest";
    public static final String SEND_ORDER_CANCELLED_NOTIFICATION = "SendOrderCancelledNotification";
    public static final String UPDATE_ORDER_STATUS_TO_CANCELLED = "UpdateOrderStatusToCancelled";


    private HandlerActivityName() {
    }

}
