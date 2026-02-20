package com.godesii.godesii_services.service.payment;

import com.godesii.godesii_services.entity.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Mock Google Pay payment service for development/testing.
 * Always returns successful mock responses without any real gateway calls.
 */
@Service
@Profile({"dev","prod"})
public class DevGooglePayPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(DevGooglePayPaymentService.class);

    @Override
    public PaymentResponse initiatePayment(Order order, String paymentMethod) {
        String mockPaymentId = "gpay_mock_" + UUID.randomUUID().toString().substring(0, 14);
        log.info("[DEV] Mock Google Pay payment initiated: {} for order: {}", mockPaymentId, order.getOrderId());

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(mockPaymentId);
        response.setGatewayOrderId(mockPaymentId);
        response.setPaymentUrl("http://localhost:8081/mock-payment/" + mockPaymentId);
        response.setStatus("PENDING");
        response.setSuccess(true);
        response.setPaymentMethod(paymentMethod);
        response.setExpiresAt(Instant.now().plusSeconds(15 * 60));

        // UPI mock data (Google Pay supports UPI in India)
        if ("UPI".equalsIgnoreCase(paymentMethod)) {
            String upiString = String.format(
                    "upi://pay?pa=merchant@godesii&pn=GoDesii&am=%.2f&tr=%s&tn=Order Payment",
                    order.getTotalAmount() / 100.0, order.getOrderId());
            response.setUpiQrCode(upiString);
            response.setUpiIntentUrl("gpay://upi/pay?" + upiString.substring(11));
            response.setMerchantVpa("merchant@godesii");
        }

        return response;
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        log.info("[DEV] Mock signature verification — auto-approved for order: {}, payment: {}", orderId, paymentId);
        return true;
    }

    @Override
    public String initiateRefund(Order order, String reason) {
        String refundId = "gpay_rfnd_mock_" + UUID.randomUUID().toString().substring(0, 14);
        log.info("[DEV] Mock refund initiated: {} for order: {}, reason: {}", refundId, order.getOrderId(), reason);
        return refundId;
    }

    @Override
    public String checkPaymentStatus(String paymentId) {
        log.info("[DEV] Mock payment status check for: {} — returning SUCCESS", paymentId);
        return "SUCCESS";
    }
}
