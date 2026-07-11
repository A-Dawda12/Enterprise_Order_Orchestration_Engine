package com.orderengine.common.web;

import com.orderengine.common.filter.ApiAccessLogFilter;
import com.orderengine.common.filter.CorrelationIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ComponentScan(basePackageClasses = {CorrelationIdFilter.class, ApiAccessLogFilter.class, GlobalExceptionHandler.class})
public class OrderCommonWebAutoConfiguration {
}
