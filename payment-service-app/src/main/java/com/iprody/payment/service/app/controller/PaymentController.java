package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistence.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private static final Logger log =
            LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public Page<PaymentDto> getAll(
            @ModelAttribute PaymentFilterDto paymentFilterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
            ) {
        log.info("GET all payments with filter: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                paymentFilterDto, page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PaymentDto> result = paymentService.getAll(paymentFilterDto, pageable);

        log.debug("Sending response Page<PaymentDto>: {}", result);
        return result;
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public PaymentDto getById(@PathVariable UUID guid) {
        log.info("GET payment by id: {}", guid);
        PaymentDto dto = paymentService.getById(guid);
        log.debug("Sending response PaymentDto: {}", dto);
        return dto;
    }

    @GetMapping("/by_status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public List<PaymentDto> getByStatus(@PathVariable PaymentStatus status){
        log.info("GET payments by status: {}", status);
        List<PaymentDto> result = paymentService.getByStatus(status);
        log.debug("Sending response List<PaymentDto>: {}", result);
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto create(@RequestBody PaymentDto dto) {
        log.info("POST create payment: {}", dto);
        PaymentDto created = paymentService.create(dto);
        log.debug("Sending response PaymentDto: {}", created);
        return created;
    }

    @PutMapping("/{guid}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto update(@PathVariable UUID guid, @RequestBody
    PaymentDto dto) {
        log.info("PUT update payment with id: {}, body: {}", guid, dto);
        PaymentDto updated = paymentService.update(guid, dto);
        log.debug("Sending response PaymentDto: {}", updated);
        return updated;
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID guid) {
        log.info("DELETE payment by id: {}", guid);
        paymentService.delete(guid);
        log.debug("Payment with id {} deleted successfully", guid);
    }

    @PatchMapping("/{guid}/note")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto updateNote(
            @PathVariable UUID guid,
            @RequestBody String note
    ) {
        log.info("PATCH update note for payment id: {}, note: {}", guid, note);
        PaymentDto updated = paymentService.updateNote(guid, note);
        log.debug("Sending response PaymentDto: {}", updated);
        return updated;
    }
}

