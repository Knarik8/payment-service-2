//package com.iprody.payment.service.app.async;
//
//import jakarta.annotation.PreDestroy;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.OffsetDateTime;
//import java.util.UUID;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage>{
//
//    private final ScheduledExecutorService scheduler =
//            Executors.newScheduledThreadPool(2);
//    private final AsyncListener<XPaymentAdapterResponseMessage>
//            resultListener;
//    private static final Logger logger = LoggerFactory.getLogger(InMemoryXPaymentAdapterMessageBroker.class);
//
//
//    @Autowired
//    public InMemoryXPaymentAdapterMessageBroker(
//            AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
//        this.resultListener = resultListener;
//    }
//    @Override
//    public void send(XPaymentAdapterRequestMessage request) {
//        UUID txId = UUID.randomUUID();
//        logger.info("📤 Sending request: paymentId={}, amount={}, currency={}, txId={}",
//                request.getPaymentGuid(), request.getAmount(), request.getCurrency(), txId);
//        scheduler.schedule(() -> emit(request, txId,
//                XPaymentAdapterStatus.PROCESSING), 0, TimeUnit.SECONDS);
//        scheduler.schedule(() -> emit(request, txId,
//                XPaymentAdapterStatus.PROCESSING), 10, TimeUnit.SECONDS);
//
//        scheduler.schedule(() -> emit(request, txId,
//                XPaymentAdapterStatus.SUCCEEDED), 20, TimeUnit.SECONDS);
//    }
//    private void emit(XPaymentAdapterRequestMessage request, UUID txId,
//                      XPaymentAdapterStatus status) {
//        XPaymentAdapterResponseMessage result = new
//                XPaymentAdapterResponseMessage();
//        result.setPaymentGuid(request.getPaymentGuid());
//        result.setAmount(request.getAmount());
//        result.setCurrency(request.getCurrency());
//        result.setTransactionRefId(txId);
//        result.setStatus(status);
//        result.setOccurredAt(OffsetDateTime.now());
//        resultListener.onMessage(result);
//    }
//    @PreDestroy
//    public void shutdown() {
//        scheduler.shutdownNow();
//    }
//}
