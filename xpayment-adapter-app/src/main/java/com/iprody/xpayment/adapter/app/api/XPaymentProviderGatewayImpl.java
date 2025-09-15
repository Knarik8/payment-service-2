package com.iprody.xpayment.adapter.app.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Service
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    public XPaymentProviderGatewayImpl(DefaultApi defaultApi) {
        this.defaultApi = defaultApi;
    }
    @Override
    public ChargeResponse createCharge(CreateChargeRequest createChargeRequest) throws RestClientException {
        try {
            return defaultApi.createCharge(createChargeRequest);
        } catch (RestClientResponseException e) {
            throw toRestClientException("POST /charges failed", e);
        }
    }
    @Override
    public ChargeResponse retrieveCharge(UUID id) throws
            RestClientException {
        try {
            return defaultApi.retrieveCharge(UUID.fromString(id.toString()));
        } catch (RestClientResponseException e) {
            throw toRestClientException("GET /charges/{id} failed (id=" + id + ")", e);
        }
    }
    private RestClientException toRestClientException(String prefix, RestClientResponseException e) {
        String msg = String.format("%s: HTTP %d, body: %s",
                prefix, e.getRawStatusCode(), safeStringConverter(e.getResponseBodyAsString()));
        return new RestClientException(msg, e);
    }

    private String safeStringConverter(String s) {
        return s == null || s.isEmpty() ? "<empty>" : s;
    }
}
