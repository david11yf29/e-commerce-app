package com.microservices.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final OrderlineMapper orderlineMapper;

    public Integer saveOrderLine(OrderLineRequest orderLineRequest) {
        var order = orderlineMapper.toOrderLine(orderLineRequest);
        return orderLineRepository.save(order).getId();
    }
}
