package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.async.AsyncSender;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.mapper.XPaymentAdapterMapper;
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
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

    @Autowired
    public PaymentService(PaymentMapper paymentMapper, PaymentRepository paymentRepository, XPaymentAdapterMapper
            xPaymentAdapterMapper, AsyncSender<XPaymentAdapterRequestMessage>
                                      sender) {
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
        this.sender = sender;
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
        PaymentDto resultDto = paymentMapper.toDto(saved);
        // Отправка сообщения
        XPaymentAdapterRequestMessage requestMessage =
                xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(entity);
        sender.send(requestMessage);

        return resultDto;
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

