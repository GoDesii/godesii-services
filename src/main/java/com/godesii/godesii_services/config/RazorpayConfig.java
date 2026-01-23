package com.godesii.godesii_services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Razorpay payment gateway configuration
 */
@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id:rzp_test_dummy_key}")
    private String keyId;

    @Value("${razorpay.key.secret:rzp_test_dummy_secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret:webhook_secret}")
    private String webhookSecret;

    @Value("${razorpay.callback.url:http://localhost:8080/api/v1/orders}")
    private String callbackUrl;

    @Value("${razorpay.enabled:false}")
    private boolean enabled;

    @Value("${razorpay.merchant.vpa:merchant@razorpay}")
    private String merchantVpa; // Merchant's Virtual Payment Address for UPI

    public String getKeyId() {
        return keyId;
    }

    public String getKeySecret() {
        return keySecret;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMerchantVpa() {
        return merchantVpa;
    }
}
