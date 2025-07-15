package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final Payment payment = new Payment(1L, 99.99);
    private final Payment payment1 = new Payment(2L, 299.99);
    private final Payment payment2 = new Payment(3L, 399.99);
    private final Payment payment3 = new Payment(4L, 499.99);
    private final Map<Long, Payment> paymentStorage = Map.of(1L, payment, 2L, payment1, 3L, payment2, 4L, payment3);

    @GetMapping
    public List<Payment> getPayments() {
        return new ArrayList<>(paymentStorage.values());
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentStorage.get(id);
    }

}
