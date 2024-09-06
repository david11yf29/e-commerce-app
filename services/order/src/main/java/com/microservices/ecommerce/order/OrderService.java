package com.microservices.ecommerce.order;

import com.microservices.ecommerce.customer.CustomerClient;
import com.microservices.ecommerce.exception.BusinessException;
import com.microservices.ecommerce.kafka.OrderConfirmation;
import com.microservices.ecommerce.kafka.OrderProducer;
import com.microservices.ecommerce.orderline.OrderLineRequest;
import com.microservices.ecommerce.orderline.OrderLineService;
import com.microservices.ecommerce.product.ProductClient;
import com.microservices.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createdOrder(OrderRequest orderRequest) {
        // Check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists with the provided ID"));

        // Purchase the Product --> Product ms (RestTemplate)
        var purchasedProducts = this.productClient.purchaseProducts(orderRequest.products());

        // Persist Order
        var order = this.orderRepository.save(orderMapper.toOrder(orderRequest));

        // Persist Order Lines
        for (PurchaseRequest purchaseRequest: orderRequest.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // TODO Start Payment Process


        // Send the Order Confirmation --> Notification ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                    orderRequest.reference(),
                        orderRequest.amount(),
                        orderRequest.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
