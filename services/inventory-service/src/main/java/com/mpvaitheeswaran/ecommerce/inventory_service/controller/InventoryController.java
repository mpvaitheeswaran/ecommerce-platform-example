package com.mpvaitheeswaran.ecommerce.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    //    Check Stock
    @GetMapping("/{productId}")
    public String getInventoryByProductId() {
        return "Inventory details by product ID";
    }

    @PostMapping("/reserve")
    public String reserveInventory() {
        // Flow:
        // 1. Validate product availability
        // 2. Reserve stock (update inventory)
        // 3. Return reservation details
        return "Inventory reserved successfully";
    }
    @PostMapping("/release")
    public String releaseInventory() {
        // Flow:
        // 1. Validate reservation
        // 2. Release stock (update inventory)
        // 3. Return release details
        return "Inventory released successfully";
    }

}
