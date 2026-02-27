package com.mpvaitheeswaran.ecommerce.api_gateway.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

   @PostMapping("/items")
    public String addItemToCart() {
       // Flow:
       // 1. Validate item details
       // 2. Add item to cart
       // 3. Return updated cart details
        return "Item added to cart successfully";
    }

    @GetMapping
    public String getCart() {
        return "Cart details";
    }

    @DeleteMapping("/items/{productId}")
    public String removeItemFromCart(@PathVariable String productId) {
        // Flow:
        // 1. Validate item ID
        // 2. Remove item from cart
        // 3. Return updated cart details
        return "Item removed from cart successfully";
    }

}
