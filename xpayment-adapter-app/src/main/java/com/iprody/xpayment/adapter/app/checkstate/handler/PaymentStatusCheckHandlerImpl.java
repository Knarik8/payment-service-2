package com.iprody.xpayment.adapter.app.checkstate.handler;

import com.iprody.xpayment.adapter.app.api.XPaymentProviderGateway;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterResponseMessage;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterStatus;
import com.iprody.xpayment.adapter.app.async.kafka.KafkaXPaymentAdapterResponseSender;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler{

    private final KafkaXPaymentAdapterResponseSender responseSender;
    private final XPaymentProviderGateway xPaymentProviderGateway;

    public PaymentStatusCheckHandlerImpl(
            KafkaXPaymentAdapterResponseSender responseSender,
            XPaymentProviderGateway xPaymentProviderGateway
    ) {
        this.responseSender = responseSender;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
    }

    @Override
    public boolean handle(UUID chargeGuid) {
        // Получаем статус платежа из XPayment через gateway
        ChargeResponse chargeResponse = xPaymentProviderGateway.retrieveCharge(chargeGuid);
        XPaymentAdapterStatus status = XPaymentAdapterStatus.valueOf(chargeResponse.getStatus());

        // Если платеж завершён, отправляем в Payment Service
        if (status == XPaymentAdapterStatus.SUCCEEDED || status == XPaymentAdapterStatus.CANCELED) {

            XPaymentAdapterResponseMessage response = new XPaymentAdapterResponseMessage();
            response.setPaymentGuid(chargeResponse.getOrder());
            response.setTransactionRefId(chargeResponse.getId());
            response.setAmount(chargeResponse.getAmount());
            response.setCurrency(chargeResponse.getCurrency());
            response.setStatus(status);
            response.setOccurredAt(OffsetDateTime.now());

            responseSender.send(response);

            return true;
        }

        // Если платеж в PROCESSING — возвращаем false
        return false;
    }
}
