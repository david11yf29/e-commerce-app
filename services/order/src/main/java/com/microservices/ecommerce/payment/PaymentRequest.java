package com.microservices.ecommerce.payment;

import com.microservices.ecommerce.customer.CustomerResponse;
import com.microservices.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
