package com.u3.shoppingcart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addItem(CartItem cartItem){
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotaleAmount();
    }

    public void removeItem(CartItem cartItem){
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
        updateTotaleAmount();
    }


    private void updateTotaleAmount() {
        this.totalAmount = this.cartItems.stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getUnitPrice();
                    if (unitPrice == null){
                        return BigDecimal.ZERO;
                    }
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
