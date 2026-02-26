package com.mpvaitheeswaran.ecommerce.payment_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping
    public String initiatePayment() {
        // Flow:
        // 1. Validate payment details
        // 2. Process payment (call payment gateway)
        // 3. Return payment status
        return "Payment initiated successfully";
    }

    @PostMapping("/webhook")
    public String paymentCallBack(){
        // Flow:
        // 1. Validate webhook payload
        // 2. Update payment status
        // 3. Publish PAYMENT_SUCCESS event
        return "Payment callback received successfully";
    }
}
