package com.orderengine.common.logging;

public final class MdcKeys {

    public static String CORRELATION_ID = "correlationId";
    public static String HTTP_METHOD = "httpMethod";
    public static String HTTP_PATH = "httpPath";
    public static String HTTP_STATUS = "httpStatus";
    public static String DURATION_MS = "durationMs";
    public static String ORDER_ID = "orderId";

    private MdcKeys() {

    }

}
