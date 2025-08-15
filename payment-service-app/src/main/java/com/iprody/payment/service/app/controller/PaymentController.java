package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistence.PaymentFilterDto;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.service.PaymentService;
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

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return paymentService.getAll(paymentFilterDto,
                pageable);
    }

    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public PaymentDto getById(@PathVariable UUID guid) {
        return paymentService.getById(guid);
    }

    @GetMapping("/by_status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','READER')")
    public List<PaymentDto> getByStatus(@PathVariable PaymentStatus status){
        return paymentService.getByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto create(@RequestBody PaymentDto dto) {
        return paymentService.create(dto);
    }

    @PutMapping("/{guid}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto update(@PathVariable UUID guid, @RequestBody
    PaymentDto dto) {
        return paymentService.update(guid, dto);
    }

    @DeleteMapping("/{guid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID guid) {
        paymentService.delete(guid);
    }

    @PatchMapping("/{guid}/note")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDto updateNote(
            @PathVariable UUID guid,
            @RequestBody String note
    ) {
        return paymentService.updateNote(guid, note);
    }
}

