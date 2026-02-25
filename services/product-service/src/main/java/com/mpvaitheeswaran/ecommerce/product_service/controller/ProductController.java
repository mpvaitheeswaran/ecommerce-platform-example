package com.mpvaitheeswaran.ecommerce.product_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping()
    public String getAllProducts(@RequestParam (required = false) String page, @RequestParam (required = false) String size) {
        return "List of all products";
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable String id) {
        return "Product details by ID " + id;
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String q) {
        return "Search results for query: " + q;
    }

    @PostMapping()
    public String createProduct() {
        // Flow: Create Product (Admin)
        // 1. Validate product details
        // 2. Save product
        // 3. Publish PRODUCT_CREATED event
        return "Product created successfully";
    }

}
