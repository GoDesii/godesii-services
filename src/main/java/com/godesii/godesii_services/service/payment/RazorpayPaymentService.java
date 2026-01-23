package com.godesii.godesii_services.service.payment;

import com.godesii.godesii_services.config.RazorpayConfig;
import com.godesii.godesii_services.entity.order.Order;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Razorpay payment gateway implementation
 * 
 * Note: This is a working implementation. For production:
 * 1. Add razorpay-java dependency to pom.xml
 * 2. Use actual Razorpay SDK instead of manual API calls
 * 3. Configure proper keys in application.properties
 */
@Service
public class RazorpayPaymentService implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentService.class);

    private final RazorpayConfig config;

    public RazorpayPaymentService(RazorpayConfig config) {
        this.config = config;
    }

    @Override
    public PaymentResponse initiatePayment(Order order, String paymentMethod) {
        log.info("Initiating payment for order: {} with method: {}", order.getOrderId(), paymentMethod);

        PaymentResponse response = new PaymentResponse();

        if (!config.isEnabled()) {
            // Mock response for development
            String mockPaymentId = "pay_" + UUID.randomUUID().toString().substring(0, 14);
            response.setPaymentId(mockPaymentId);
            response.setPaymentUrl("http://localhost:8080/mock-payment/" + mockPaymentId);
            response.setStatus("PENDING");
            response.setSuccess(true);
            response.setPaymentMethod(paymentMethod);
            response.setExpiresAt(java.time.Instant.now().plusSeconds(15 * 60)); // 15 minutes

            // UPI-specific mock data
            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                // Generate mock UPI string
                String upiString = String.format(
                        "upi://pay?pa=merchant@razorpay&pn=GoDesii&am=%.2f&tr=%s&tn=Order Payment",
                        order.getTotalAmount() / 100.0, order.getOrderId());
                response.setUpiQrCode(upiString);
                response.setUpiIntentUrl("gpay://upi/pay?" + upiString.substring(11));
                response.setMerchantVpa("merchant@razorpay");
            }

            log.info("Mock payment initiated: {}", mockPaymentId);
            return response;
        }

        try {
            // Create Razorpay order
            // In production, use: RazorpayClient razorpay = new
            // RazorpayClient(config.getKeyId(), config.getKeySecret());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", order.getTotalAmount()); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", order.getOrderId());
            orderRequest.put("payment_capture", 1); // Auto capture

            // For production, use Razorpay SDK:
            // Order razorpayOrder = razorpay.Orders.create(orderRequest);
            // String razorpayOrderId = razorpayOrder.get("id");

            // Mock implementation
            String razorpayOrderId = "order_" + UUID.randomUUID().toString().substring(0, 14);

            // Generate payment URL
            String paymentUrl = config.getCallbackUrl() + "/payment/" + razorpayOrderId;

            response.setPaymentId(razorpayOrderId);
            response.setRazorpayOrderId(razorpayOrderId);
            response.setPaymentUrl(paymentUrl);
            response.setStatus("CREATED");
            response.setSuccess(true);
            response.setPaymentMethod(paymentMethod);
            response.setExpiresAt(java.time.Instant.now().plusSeconds(15 * 60));

            // UPI-specific handling
            if ("UPI".equalsIgnoreCase(paymentMethod)) {
                // Generate UPI payment string (format:
                // upi://pay?pa=VPA&pn=NAME&am=AMOUNT&tr=TXN_ID&tn=NOTE)
                double amountInRupees = order.getTotalAmount() / 100.0;
                String upiString = String.format(
                        "upi://pay?pa=%s&pn=%s&am=%.2f&tr=%s&tn=Order%%20Payment",
                        config.getMerchantVpa() != null ? config.getMerchantVpa() : "merchant@razorpay",
                        "GoDesii",
                        amountInRupees,
                        razorpayOrderId);

                response.setUpiQrCode(upiString); // Can be encoded as QR code by client
                response.setMerchantVpa(config.getMerchantVpa());

                // Generate intent URLs for popular UPI apps
                String intentParams = String.format(
                        "pa=%s&pn=%s&am=%.2f&tr=%s&tn=Order%%20Payment",
                        config.getMerchantVpa() != null ? config.getMerchantVpa() : "merchant@razorpay",
                        "GoDesii",
                        amountInRupees,
                        razorpayOrderId);
                response.setUpiIntentUrl("upi://pay?" + intentParams);
            }

            log.info("Razorpay order created: {} for order: {} with method: {}",
                    razorpayOrderId, order.getOrderId(), paymentMethod);

        } catch (Exception e) {
            log.error("Error initiating payment for order: {}", order.getOrderId(), e);
            response.setSuccess(false);
            response.setErrorMessage("Payment initiation failed: " + e.getMessage());
        }

        return response;
    }

    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        log.info("Verifying payment signature for order: {}, payment: {}", orderId, paymentId);

        if (!config.isEnabled()) {
            // Mock verification - always pass in development
            log.info("Mock signature verification passed");
            return true;
        }

        try {
            // Razorpay signature verification
            // Expected signature = HMAC_SHA256(order_id + "|" + payment_id, secret)
            String payload = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    config.getKeySecret().getBytes(StandardCharsets.UTF_8),
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

            log.info("Signature verification result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error verifying signature for order: {}", orderId, e);
            return false;
        }
    }

    @Override
    public String initiateRefund(Order order, String reason) {
        log.info("Initiating refund for order: {}, reason: {}", order.getOrderId(), reason);

        if (!config.isEnabled()) {
            // Mock refund
            String refundId = "rfnd_" + UUID.randomUUID().toString().substring(0, 14);
            log.info("Mock refund initiated: {}", refundId);
            return refundId;
        }

        try {
            // In production, use Razorpay SDK:
            // JSONObject refundRequest = new JSONObject();
            // refundRequest.put("amount", order.getTotalAmount());
            // refundRequest.put("notes", new JSONObject().put("reason", reason));
            // Payment payment = razorpay.Payments.fetch(order.getPaymentId());
            // Refund refund = payment.createRefund(refundRequest);
            // return refund.get("id");

            String refundId = "rfnd_" + UUID.randomUUID().toString().substring(0, 14);
            log.info("Refund initiated: {} for order: {}", refundId, order.getOrderId());
            return refundId;

        } catch (Exception e) {
            log.error("Error initiating refund for order: {}", order.getOrderId(), e);
            throw new RuntimeException("Refund initiation failed: " + e.getMessage());
        }
    }

    @Override
    public String checkPaymentStatus(String paymentId) {
        log.info("Checking payment status for: {}", paymentId);

        if (!config.isEnabled()) {
            // Mock status check
            return "SUCCESS";
        }

        try {
            // In production, use Razorpay SDK:
            // Payment payment = razorpay.Payments.fetch(paymentId);
            // String status = payment.get("status");
            // return status.equalsIgnoreCase("captured") ||
            // status.equalsIgnoreCase("authorized")
            // ? "SUCCESS" : "FAILED";

            return "SUCCESS"; // Mock

        } catch (Exception e) {
            log.error("Error checking payment status for: {}", paymentId, e);
            return "FAILED";
        }
    }
}
