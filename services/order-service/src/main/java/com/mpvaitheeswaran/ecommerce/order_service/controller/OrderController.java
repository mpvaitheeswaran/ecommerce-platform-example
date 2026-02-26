package com.mpvaitheeswaran.ecommerce.order_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/orders")
public class OrderController {

    @GetMapping
    public String getOrders() {
        return "List of orders";
    }

    @PostMapping
    public String createOrder() {
//        Flow:
//        1. Validate cart
//        2. Call inventory-service (sync)
//        3. Call payment-service (sync or initiate)
//        4. Save order
//        5. Publish ORDER_CREATED event
        return "Order created successfully";
    }

    @GetMapping("/{orderId}")
    public String getOrderById() {
        return "Order details by ID";
    }




}
