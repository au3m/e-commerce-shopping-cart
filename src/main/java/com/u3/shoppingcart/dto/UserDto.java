package com.u3.shoppingcart.dto;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<OrderItemDto> orders;
    private List<CartItemDto> cart;
}
