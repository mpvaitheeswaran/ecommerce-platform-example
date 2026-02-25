package com.mpvaitheeswaran.ecommerce.user_service.controller;

import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
public class UserController {

    @GetMapping()
    public String getUsers() {
        return "List of users";
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable String id) {
        return "User details by ID " + id;
    }

    @PutMapping("/{id}")
    public String updateUserById(@PathVariable String id) {
        return "User details updated for ID " + id;
    }

    @GetMapping("/{id}/addresses")
    public String getUserAddressesById(@PathVariable String id) {
        return "User addresses by ID " + id;
    }
}
