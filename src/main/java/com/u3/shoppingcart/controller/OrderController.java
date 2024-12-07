package com.u3.shoppingcart.controller;

import com.u3.shoppingcart.dto.OrderDto;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Order;
import com.u3.shoppingcart.response.ApiResponse;
import com.u3.shoppingcart.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> createOrder(@RequestParam Long userId){
        try {
            Order order = orderService.placeOrder(userId);
            OrderDto orderDto = orderService.convertToDto(order);
            return ResponseEntity.ok(new ApiResponse("Item Order Success!", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error Occured!", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}/order")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId){
        try {
            List<OrderDto> order = orderService.getUserOrders(orderId);
            return ResponseEntity.ok(new ApiResponse("Item Order Success!", order));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Oops!", e.getMessage()));
        }
    }



}
