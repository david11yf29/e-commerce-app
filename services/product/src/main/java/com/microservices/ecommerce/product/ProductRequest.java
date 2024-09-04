package com.microservices.ecommerce.product;

import com.microservices.ecommerce.category.Category;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        Integer Id,

        @NotNull(message = "Product name is required")
        String name,

        @NotNull(message = "Product description  is required")
        String description,

        @Positive(message = "Available quantity should be positive")
        double availableQuantity,

        @Positive(message = "Price should be positive")
        BigDecimal price,

        @NotNull(message = "Product category is required")
        Integer categoryId
) {
}
