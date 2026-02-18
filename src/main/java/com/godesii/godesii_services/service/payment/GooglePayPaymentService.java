package com.godesii.godesii_services.service.payment;

import com.godesii.godesii_services.config.GooglePayConfig;
import com.godesii.godesii_services.entity.order.Order;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Google Pay payment gateway implementation
 *
 * In production:
 * 1. The client-side Google Pay button collects payment credentials
 * 2. The signed payment token is sent to this backend
 * 3. Backend verifies the token and processes via the PSP gateway
 * 4. Refunds are handled through the PSP gateway, not Google Pay directly
 */
@Service
    @Profile("!prod")
public class GooglePayPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(GooglePayPaymentService.class);

    private final GooglePayConfig config;

    public GooglePayPaymentService(GooglePayConfig config) {
        this.config = config;
    }

    @Override
    public PaymentResponse initiatePayment(Order order, String paymentMethod) {
        log.info("Initiating Google Pay payment for order: {} with method: {}", order.getOrderId(), paymentMethod);

        PaymentResponse response = new PaymentResponse();

        if (!config.isEnabled()) {
            // Mock response for local/dev testing
            String mockPaymentId = "gpay_" + UUID.randomUUID().toString().substring(0, 14);
            response.setPaymentId(mockPaymentId);
            response.setPaymentUrl("http://localhost:8081/mock-payment/" + mockPaymentId);
            response.setStatus("PENDING");
            response.setSuccess(true);
            response.setPaymentMethod(paymentMethod);
            response.setExpiresAt(java.time.Instant.now().plusSeconds(15 * 60)); // 15 minutes

            // Google Pay specific mock data
            response.setGatewayOrderId(mockPaymentId);

            // UPI-specific mock data (Google Pay supports UPI in India)
            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                String upiString = String.format(
                        "upi://pay?pa=merchant@godesii&pn=GoDesii&am=%.2f&tr=%s&tn=Order Payment",
                        order.getTotalAmount() / 100.0, order.getOrderId());
                response.setUpiQrCode(upiString);
                response.setUpiIntentUrl("gpay://upi/pay?" + upiString.substring(11));
                response.setMerchantVpa("merchant@godesii");
            }

            log.info("Mock Google Pay payment initiated: {}", mockPaymentId);
            return response;
        }

        try {
            // Production: Create a payment session for Google Pay

            String gatewayOrderId = "gpay_order_" + UUID.randomUUID().toString().substring(0, 14);
            String paymentUrl = config.getCallbackUrl() + "/payment/" + gatewayOrderId;

            response.setPaymentId(gatewayOrderId);
            response.setGatewayOrderId(gatewayOrderId);
            response.setPaymentUrl(paymentUrl);
            response.setStatus("CREATED");
            response.setSuccess(true);
            response.setPaymentMethod(paymentMethod);
            response.setExpiresAt(java.time.Instant.now().plusSeconds(15 * 60));

            // UPI-specific handling (Google Pay is a popular UPI app in India)
            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                double amountInRupees = order.getTotalAmount() / 100.0;
                String upiString = String.format(
                        "upi://pay?pa=%s&pn=%s&am=%.2f&tr=%s&tn=Order%%20Payment",
                        "merchant@godesii",
                        config.getMerchantName(),
                        amountInRupees,
                        gatewayOrderId);

                response.setUpiQrCode(upiString);
                response.setMerchantVpa("merchant@godesii");

                String intentParams = String.format(
                        "pa=%s&pn=%s&am=%.2f&tr=%s&tn=Order%%20Payment",
                        "merchant@godesii",
                        config.getMerchantName(),
                        amountInRupees,
                        gatewayOrderId);
                response.setUpiIntentUrl("upi://pay?" + intentParams);
            }

            log.info("Google Pay order created: {} for order: {} with method: {}",
                    gatewayOrderId, order.getOrderId(), paymentMethod);

        } catch (Exception e) {
            log.error("Error initiating Google Pay payment for order: {}", order.getOrderId(), e);
            response.setSuccess(false);
            response.setErrorMessage("Google Pay payment initiation failed: " + e.getMessage());
        }

        return response;
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        log.info("Verifying Google Pay payment signature for order: {}, payment: {}", orderId, paymentId);

        if (!config.isEnabled()) {
            // Mock verification — always pass in dev/test
            log.info("Mock Google Pay signature verification passed");
            return true;
        }

        try {
            // Google Pay token verification:
            // In production, the payment token from Google Pay is a signed JSON object.
            // Verification involves:
            // 1. Verify the signature using Google's root signing keys
            // 2. Verify the token has not expired
            // 3. Decrypt the payment credentials
            // 4. Process payment through the PSP gateway

            // For now, verify using HMAC-SHA256 with the gateway merchant ID as secret
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    config.getGatewayMerchantId().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            String expectedSignature = hexString.toString();
            boolean isValid = expectedSignature.equals(signature);

            log.info("Google Pay signature verification result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying Google Pay signature for order: {}", orderId, e);
            return false;
        }
    }

    @Override
    public String initiateRefund(Order order, String reason) {
        log.info("Initiating refund via Google Pay gateway for order: {}, reason: {}", order.getOrderId(), reason);

        if (!config.isEnabled()) {
            // Mock refund
            String refundId = "gpay_rfnd_" + UUID.randomUUID().toString().substring(0, 14);
            log.info("Mock Google Pay refund initiated: {}", refundId);
            return refundId;
        }

        try {
            // In production, refunds are processed through the PSP gateway (e.g., Stripe,
            // Adyen)
            // that was used to process the original Google Pay payment token.
            // The PSP handles the actual fund reversal.

            String refundId = "gpay_rfnd_" + UUID.randomUUID().toString().substring(0, 14);
            log.info("Google Pay refund initiated: {} for order: {}", refundId, order.getOrderId());
            return refundId;

        } catch (Exception e) {
            log.error("Error initiating refund for order: {}", order.getOrderId(), e);
            throw new RuntimeException("Google Pay refund initiation failed: " + e.getMessage());
        }
    }

    @Override
    public String checkPaymentStatus(String paymentId) {
        log.info("Checking Google Pay payment status for: {}", paymentId);

        if (!config.isEnabled()) {
            // Mock status check
            return "SUCCESS";
        }

        try {
            // In production, query the PSP gateway for the payment status
            // using the transaction ID that was created when processing the Google Pay
            // token.
            return "SUCCESS";

        } catch (Exception e) {
            log.error("Error checking Google Pay payment status for: {}", paymentId, e);
            return "FAILED";
        }
    }

    /**
     * Build Google Pay payment configuration for the client
     * This JSON is sent to the frontend to initialize the Google Pay button
     */
    private JsonObject buildGooglePayConfig(Order order) {
        JsonObject config = new JsonObject();
        config.addProperty("apiVersion", 2);
        config.addProperty("apiVersionMinor", 0);

        // Merchant info
        JsonObject merchantInfo = new JsonObject();
        merchantInfo.addProperty("merchantId", this.config.getMerchantId());
        merchantInfo.addProperty("merchantName", this.config.getMerchantName());
        config.add("merchantInfo", merchantInfo);

        // Transaction info
        JsonObject transactionInfo = new JsonObject();
        transactionInfo.addProperty("totalPriceStatus", "FINAL");
        transactionInfo.addProperty("totalPrice", String.format("%.2f", order.getTotalAmount() / 100.0));
        transactionInfo.addProperty("currencyCode", "INR");
        transactionInfo.addProperty("countryCode", "IN");
        config.add("transactionInfo", transactionInfo);

        // Tokenization specification
        JsonObject tokenizationSpec = new JsonObject();
        tokenizationSpec.addProperty("type", "PAYMENT_GATEWAY");
        JsonObject parameters = new JsonObject();
        parameters.addProperty("gateway", this.config.getGatewayName());
        parameters.addProperty("gatewayMerchantId", this.config.getGatewayMerchantId());
        tokenizationSpec.add("parameters", parameters);
        config.add("tokenizationSpecification", tokenizationSpec);

        return config;
    }
}
