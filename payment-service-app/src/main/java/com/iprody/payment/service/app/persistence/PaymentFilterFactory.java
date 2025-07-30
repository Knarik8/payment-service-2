package com.iprody.payment.service.app.persistence;

import com.iprody.payment.service.app.persistence.entity.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PaymentFilterFactory {

    private static final Specification<Payment> EMPTY = (root, query, criteriaBuilder) -> null;

    public static Specification<Payment> fromFilter(PaymentFilterDto paymentFilterDto) {

        Specification<Payment> specification = EMPTY;

        if (StringUtils.hasText(paymentFilterDto.getCurrency())) {
            specification = specification.and(PaymentSpecifications.hasCurrency(paymentFilterDto.getCurrency()));
        }

        if (paymentFilterDto.getMinAmount() != null && paymentFilterDto.getMaxAmount() != null) {
            specification = specification.and(PaymentSpecifications.amountBetween(
                    paymentFilterDto.getMinAmount(), paymentFilterDto.getMaxAmount()));
        }

        if (paymentFilterDto.getPaymentStatus() != null) {
            specification = specification.and(PaymentSpecifications.hasStatus(paymentFilterDto.getPaymentStatus()));
        }

        if (paymentFilterDto.getCreatedAfter() != null && paymentFilterDto.getCreatedBefore() != null) {
            specification = specification.and(PaymentSpecifications.createdBetween(
                    paymentFilterDto.getCreatedAfter(), paymentFilterDto.getCreatedBefore()));
        }

        if (paymentFilterDto.getCreatedAfter() != null) {
            specification = specification.and(PaymentSpecifications.createdAfter(paymentFilterDto.getCreatedAfter()));
        }

        if (paymentFilterDto.getCreatedBefore() != null) {
            specification = specification.and(PaymentSpecifications.createdBefore(paymentFilterDto.getCreatedBefore()));
        }

        return specification;
    }
}
