package com.microservices.ecommerce.order;

import com.microservices.ecommerce.customer.CustomerClient;
import com.microservices.ecommerce.exception.BusinessException;
import com.microservices.ecommerce.orderline.OrderLineRequest;
import com.microservices.ecommerce.orderline.OrderLineService;
import com.microservices.ecommerce.product.ProductClient;
import com.microservices.ecommerce.product.PurchaseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper orderMapper;
    private final OrderLineService orderLineService;

    public Integer createdOrder(OrderRequest orderRequest) {
        // Check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(orderRequest.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No Customer exists with the provided ID"));

        // Purchase the Product --> Product ms (RestTemplate)
        this.productClient.purchaseProducts(orderRequest.products());

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
        return null;
    }
}
