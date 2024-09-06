package com.microservices.ecommerce.kafka;

import com.microservices.ecommerce.customer.CustomerResponse;
import com.microservices.ecommerce.order.PaymentMethod;
import com.microservices.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(

        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customerResponse,
        List<PurchaseResponse> products
) {
}
