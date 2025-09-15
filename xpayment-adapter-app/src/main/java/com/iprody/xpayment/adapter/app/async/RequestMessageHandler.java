package com.iprody.xpayment.adapter.app.async;

import com.iprody.xpayment.adapter.app.api.XPaymentProviderGateway;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage>{

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Logger log = LoggerFactory.getLogger(RequestMessageHandler.class);
    private final XPaymentProviderGateway xPaymentProviderGateway;


    @Autowired
    public RequestMessageHandler(
            XPaymentProviderGateway xPaymentProviderGateway,
            AsyncSender<XPaymentAdapterResponseMessage> asyncSender) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.sender = asyncSender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Received request: messageId - {}, amount - {}, currency - {}", 
                message.getMessageId(), message.getAmount(), message.getCurrency());
        CreateChargeRequest createChargeRequest = new
                CreateChargeRequest();
        createChargeRequest.setAmount(message.getAmount());
        createChargeRequest.setCurrency(message.getCurrency());
        createChargeRequest.setOrder(message.getPaymentGuid());
        try {
            ChargeResponse chargeResponse =

                    xPaymentProviderGateway.createCharge(createChargeRequest);

            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
            chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));

            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }

    }
    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
