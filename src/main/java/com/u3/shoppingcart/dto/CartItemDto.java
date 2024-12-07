package com.u3.shoppingcart.dto;

import com.u3.shoppingcart.model.Product;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CartItemDto {
    private Long itemId;
    private int quantity;
    private BigDecimal unitPrice;
    private ProductDto product;


}
