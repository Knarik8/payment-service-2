package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.PaymentFilterFactory;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.repository.PaymentRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("Payment not found", "getById", guid));
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

    public PaymentDto create(PaymentDto dto) {
        Payment entity = paymentMapper.toEntity(dto);
        Payment saved = paymentRepository.save(entity);
        return paymentMapper.toDto(saved);
    }

    public PaymentDto update(UUID guid, PaymentDto dto) {
        Payment existing = paymentRepository.findById(guid)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found", "update", guid));
        Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(guid);
        Payment saved = paymentRepository.save(updated);
        return paymentMapper.toDto(saved);
    }

    public void delete(UUID guid) {
        if (!paymentRepository.existsById(guid)) {
            throw new EntityNotFoundException("Payment not found", "delete", guid);
        }
        paymentRepository.deleteById(guid);
    }

    public PaymentDto updateNote(UUID guid, String newNote) {
        PaymentDto dto = getById(guid);
        dto.setNote(newNote);
        return update(guid, dto);
    }
}

