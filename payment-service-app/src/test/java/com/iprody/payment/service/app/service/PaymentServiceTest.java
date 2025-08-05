package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistence.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;
    private UUID inquiryRefId;
    private UUID transactionRefId;
    private OffsetDateTime now;
    private Payment payment2;
    private PaymentDto paymentDto2;
    private UUID guid2;
    private UUID inquiryRefId2;
    private UUID transactionRefId2;


    @BeforeEach
    void setUp() {

        guid = UUID.randomUUID();
        inquiryRefId = UUID.randomUUID();
        transactionRefId = UUID.randomUUID();
        now = OffsetDateTime.now();
        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(new BigDecimal("999.99"));
        payment.setCurrency("EUR");
        payment.setTransactionRefId(transactionRefId);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote("note");
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setInquiryRefId(payment.getInquiryRefId());
        paymentDto.setTransactionRefId(payment.getTransactionRefId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setNote(payment.getNote());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());


        guid2 = UUID.randomUUID();
        inquiryRefId2 = UUID.randomUUID();
        transactionRefId2 = UUID.randomUUID();
        now = OffsetDateTime.now();
        payment2 = new Payment();
        payment2.setGuid(guid2);
        payment2.setInquiryRefId(inquiryRefId2);
        payment2.setAmount(new BigDecimal("3999.99"));
        payment2.setCurrency("RUB");
        payment2.setTransactionRefId(transactionRefId2);
        payment2.setStatus(PaymentStatus.APPROVED);
        payment2.setNote("note");
        payment2.setCreatedAt(now);
        payment2.setUpdatedAt(now);

        paymentDto2 = new PaymentDto();
        paymentDto2.setGuid(payment2.getGuid());
        paymentDto2.setInquiryRefId(payment2.getInquiryRefId());
        paymentDto2.setTransactionRefId(payment2.getTransactionRefId());
        paymentDto2.setAmount(payment2.getAmount());
        paymentDto2.setCurrency(payment2.getCurrency());
        paymentDto2.setStatus(payment2.getStatus());
        paymentDto2.setNote(payment2.getNote());
        paymentDto2.setCreatedAt(payment2.getCreatedAt());
        paymentDto2.setUpdatedAt(payment2.getUpdatedAt());

    }

    @Test
    void shouldReturnPaymentById() {

        //given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.getById(guid);

        //then
        assertEquals(payment.getGuid(), result.getGuid());
        assertEquals(payment.getInquiryRefId(), result.getInquiryRefId());
        assertEquals(payment.getTransactionRefId(), result.getTransactionRefId());
        assertEquals(payment.getAmount(), result.getAmount());
        assertEquals(payment.getCurrency(), result.getCurrency());
        assertEquals(payment.getStatus(), result.getStatus());
        assertEquals(payment.getNote(), result.getNote());
        assertEquals(payment.getCreatedAt(), result.getCreatedAt());
        assertEquals(payment.getUpdatedAt(), result.getUpdatedAt());

        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void shouldThrowEntityNotFoundException() {

        // given
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> paymentService.getById(guid)
        );

        //then
        assertTrue(ex.getMessage().contains("Payment not found with id " + guid));
        verify(paymentRepository).findById(guid);
    }

    @Test
    void shouldReturnPagedPaymentDtos() {

        // given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("amount").ascending());
        PaymentFilterDto filter = new PaymentFilterDto();

        List<Payment> list = List.of(payment, payment2);
        Page<Payment> page = new PageImpl<>(list, pageable, 10);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(paymentMapper.toDto(payment2)).thenReturn(paymentDto2);

        // when
        Page<PaymentDto> result = paymentService.getAll(filter, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getNumberOfElements());
        assertEquals(10, result.getTotalElements());
        assertEquals(paymentDto, result.getContent().get(0));
        assertEquals(paymentDto2, result.getContent().get(1));
        assertTrue(result.getContent().get(0).getAmount().compareTo(result.getContent().get(1).getAmount()) < 0);

        verify(paymentRepository).findAll((Specification<Payment>) any(), eq(pageable));
        verify(paymentMapper).toDto(payment);
        verify(paymentMapper).toDto(payment2);
    }

    @Test
    void shouldFilterAndSortPayments() {

        //given
        String currency = "EUR";
        BigDecimal minAmount = new BigDecimal("100.00");
        BigDecimal maxAmount = new BigDecimal("5000.00");
        Sort sort = Sort.by("createdAt").ascending();

        payment.setCurrency(currency);
        payment2.setCurrency(currency);

        List<Payment> payments = List.of(payment, payment2);
        List<PaymentDto> dtos = List.of(paymentDto, paymentDto2);
        Pageable pageable = PageRequest.of(0, 25, sort);
        Page<Payment> page = new PageImpl<>(payments, pageable, payments.size());

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);
        when(paymentMapper.toDto(payment2)).thenReturn(paymentDto2);

        PaymentFilterDto filter = new PaymentFilterDto();
        filter.setCurrency(currency);
        filter.setMinAmount(minAmount);
        filter.setMaxAmount(maxAmount);

        // when
        Page<PaymentDto> result = paymentService.getAll(filter, pageable);

        // then
        assertNotNull(result);
        assertEquals(dtos.size(), result.getNumberOfElements());
        assertEquals(dtos, result.getContent());
        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
        verify(paymentMapper).toDto(payment);
        verify(paymentMapper).toDto(payment2);
    }


    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {

        //given
        payment.setStatus(status);
        paymentDto.setStatus(status);
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //when
        PaymentDto result = paymentService.getById(guid);

        //then
        assertEquals(status, result.getStatus());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.RECEIVED,
                PaymentStatus.PENDING,
                PaymentStatus.APPROVED,
                PaymentStatus.DECLINED,
                PaymentStatus.NOT_SENT
        );
    }

}

