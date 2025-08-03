package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper, PaymentRepository paymentRepository) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
    }

    public PaymentDto getById(UUID guid) {
        Payment payment = paymentRepository.findById(guid)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id " + guid));
        return paymentMapper.toDto(payment);
    }

    public Page<PaymentDto> getAll(PaymentFilterDto filterDto, Pageable pageable) {
        Page<Payment> page = paymentRepository.findAll(
                PaymentFilterFactory.fromFilter(filterDto),
                pageable);
        return page.map(paymentMapper::toDto);
    }

    public List<PaymentDto> getByStatus(PaymentStatus status) {
        List<Payment> list = paymentRepository.findByStatus(status);
        return list.stream()
                .map(paymentMapper::toDto)
                .toList();
    }
}
