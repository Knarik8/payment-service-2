package com.iprody.payment.service.app.persistence;

import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public final class PaymentSpecifications {

    public static Specification<Payment> hasStatus(PaymentStatus paymentStatus){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), paymentStatus);
    }

    public static Specification<Payment> createdAfter(Instant after){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), after);
    }

    public static Specification<Payment> createdBefore(Instant before){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), before);
    }

    public static Specification<Payment> hasCurrency(String currency) {
        return (root, query, cb) ->
                cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) ->
                cb.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> createdBetween(Instant after, Instant before) {
        return (root, query, cb) ->
                cb.between(root.get("createdAt"), after, before);
    }
}
