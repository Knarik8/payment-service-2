package com.iprody.payment.service.app.async;

/**
 * Статусы в которых может пребывать платежная транзакция X Payment
 Adapter.
 */
public enum XPaymentAdapterStatus {

    PROCESSING,
    CANCELED,
    SUCCEEDED
}
