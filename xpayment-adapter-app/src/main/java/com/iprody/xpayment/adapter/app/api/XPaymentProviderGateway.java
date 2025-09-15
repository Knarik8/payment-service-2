package com.iprody.xpayment.adapter.app.api;

import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {

    ChargeResponse createCharge(CreateChargeRequest createChargeRequest)
            throws RestClientException;
    ChargeResponse retrieveCharge(UUID id) throws RestClientException;

}
