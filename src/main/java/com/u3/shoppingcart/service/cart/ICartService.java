package com.u3.shoppingcart.service.cart;

import com.u3.shoppingcart.model.Cart;
import com.u3.shoppingcart.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);
    Cart initializeNewCart(User user);
    Cart getCartByUserId(Long userId);
}

