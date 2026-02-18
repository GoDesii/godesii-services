package com.godesii.godesii_services.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Google Pay payment gateway configuration
 */
@Configuration
@Profile("!prod")
public class GooglePayConfig {

    @Value("${googlepay.merchant-id:TEST_MERCHANT_ID}")
    private String merchantId;

    @Value("${googlepay.merchant-name:GoDesii}")
    private String merchantName;

    @Value("${googlepay.environment:TEST}")
    private String environment; // TEST or PRODUCTION

    @Value("${googlepay.enabled:false}")
    private boolean enabled;

    @Value("${googlepay.callback-url:http://localhost:8081/api/v1/orders}")
    private String callbackUrl;

    @Value("${googlepay.supported-networks:VISA,MASTERCARD,AMEX}")
    private String supportedNetworks;

    @Value("${googlepay.gateway-name:example}")
    private String gatewayName; // PSP gateway name (e.g., stripe, adyen)

    @Value("${googlepay.gateway-merchant-id:exampleGatewayMerchantId}")
    private String gatewayMerchantId; // PSP merchant ID

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getSupportedNetworks() {
        return supportedNetworks;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public String getGatewayMerchantId() {
        return gatewayMerchantId;
    }
}
