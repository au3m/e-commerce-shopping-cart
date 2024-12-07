package com.u3.shoppingcart.service.order;

import com.u3.shoppingcart.dto.OrderDto;
import com.u3.shoppingcart.enums.OrderStatus;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Cart;
import com.u3.shoppingcart.model.Order;
import com.u3.shoppingcart.model.OrderItem;
import com.u3.shoppingcart.model.Product;
import com.u3.shoppingcart.repository.OrderRepository;
import com.u3.shoppingcart.repository.ProductRepository;
import com.u3.shoppingcart.service.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        return savedOrder;
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order.setUser(cart.getUser());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getCartItems().stream()
                .map(cartItems -> {
                    Product product = cartItems.getProduct();
                    product.setInventory(product.getInventory() - cartItems.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItems.getQuantity(),
                            cartItems.getUnitPrice()
                    );
                }).toList();
    }


    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }





    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNOtFoundException("No order found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }

}
