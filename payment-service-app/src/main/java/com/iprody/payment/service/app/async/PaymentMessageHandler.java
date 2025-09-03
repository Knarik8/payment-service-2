package com.iprody.payment.service.app.async;

import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistence.entity.PaymentStatus;
import com.iprody.payment.service.app.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage>{

    private PaymentRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentMessageHandler.class);

    @Autowired
    public PaymentMessageHandler(PaymentRepository repository) {
        this.repository = repository;
    }


    private PaymentStatus mapStatus(XPaymentAdapterStatus adapterStatus) {
        return switch (adapterStatus) {
            case SUCCEEDED -> PaymentStatus.RECEIVED;
            case PROCESSING -> PaymentStatus.PENDING;
            case CANCELED -> PaymentStatus.DECLINED;
        };
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        logger.info("Received response: paymentId={}, status={}, txId={}",
                message.getPaymentGuid(), message.getStatus(), message.getTransactionRefId());

        Payment payment = repository.getReferenceById(message.getPaymentGuid());
        payment.setStatus(mapStatus(message.getStatus()));
        payment.setTransactionRefId(message.getTransactionRefId());

        repository.save(payment);

        logger.info("Payment status updated: paymentId={}, newStatus={}",
                payment.getGuid(), payment.getStatus());
    }

}
