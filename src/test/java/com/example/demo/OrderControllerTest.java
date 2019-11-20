package com.example.demo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.example.demo.controllers.OrderController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setup()
    {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
    }

    @Test
    public void submitOrder_happy_path()
    {
        User user  = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        Item item = new Item();
        item.setId(0L);

        List<Item> items = Arrays.asList(item, item, item);
        cart.setItems(items);

        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().getItems().size());
        assertEquals("test", response.getBody().getUser().getUsername());
        assertEquals(cart.getTotal(), response.getBody().getTotal());

    }


    @Test
    public void submitOrder_user_not_found()
    {
        User user  = null;

        when(userRepo.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

    }


    @Test
    public void getOrdersForUser_happy_path()
    {
        User user  = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        Item item = new Item();
        item.setId(0L);

        List<Item> items = Arrays.asList(item, item, item);
        cart.setItems(items);

        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);

        UserOrder userOrder = UserOrder.createFromCart(cart);

        List<UserOrder> userOrders = Arrays.asList(userOrder);

        when(orderRepo.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(3, response.getBody().get(0).getItems().size());
        assertEquals("test", response.getBody().get(0).getUser().getUsername());
        assertEquals(cart.getTotal(), response.getBody().get(0).getTotal());

    }



    @Test
    public void getOrdersForUser_user_not_found()
    {
        User user  = null;

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        Item item = new Item();
        item.setId(0L);

        List<Item> items = Arrays.asList(item, item, item);
        cart.setItems(items);

        when(userRepo.findByUsername("test")).thenReturn(user);

        UserOrder userOrder = UserOrder.createFromCart(cart);

        List<UserOrder> userOrders = Arrays.asList(userOrder);

        when(orderRepo.findByUser(user)).thenReturn(userOrders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertEquals(404, response.getStatusCodeValue());

    }

}