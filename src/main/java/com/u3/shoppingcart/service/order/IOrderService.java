package com.u3.shoppingcart.service.order;

import com.u3.shoppingcart.dto.OrderDto;
import com.u3.shoppingcart.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
