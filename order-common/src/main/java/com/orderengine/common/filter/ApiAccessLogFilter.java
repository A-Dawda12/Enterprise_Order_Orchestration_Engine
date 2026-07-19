package com.orderengine.common.filter;

import com.orderengine.common.logging.MdcKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class ApiAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiAccessLogFilter.class);
    private static final Pattern ORDER_ID_PATH = Pattern.compile("/v1/orders/([^/]+)");

    @Override
    protected void doFilterInternal(
           HttpServletRequest request,
           HttpServletResponse response,
           FilterChain filterChain
    ) throws ServletException, IOException {
        long startNanos = System.nanoTime();
        String method = request.getMethod();
        String path = request.getRequestURI();

        MDC.put(MdcKeys.HTTP_METHOD, method);
        MDC.put(MdcKeys.HTTP_PATH, path);
        extractOrderId(path).ifPresent(orderId -> MDC.put(MdcKeys.ORDER_ID, orderId));

        log.info("API IN method = {}, path = {}", method, path);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
            int status = response.getStatus();

            MDC.put(MdcKeys.HTTP_STATUS, String.valueOf(status));
            MDC.put(MdcKeys.DURATION_MS, String.valueOf(durationMs));

            log.info("API OUT method = {}, path = {}, status = {}, durationMs = {}", method, path, status, durationMs);

            MDC.remove(MdcKeys.HTTP_METHOD);
            MDC.remove(MdcKeys.HTTP_PATH);
            MDC.remove(MdcKeys.HTTP_STATUS);
            MDC.remove(MdcKeys.DURATION_MS);
            MDC.remove(MdcKeys.ORDER_ID);
        }
    }

    private static Optional<String> extractOrderId(String path) {
        Matcher matcher = ORDER_ID_PATH.matcher(path);
        if (matcher.find()) {
            String candidate = matcher.group(1);
            if(!"health".equals(candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }
}
