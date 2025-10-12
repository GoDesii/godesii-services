package com.godesii.godesii_services.config.twilio;

import com.twilio.Twilio;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {

    private final TwilioConfig twiloConfiguration;

    public TwilioInitializer(TwilioConfig twiloConfiguration) {
        this.twiloConfiguration = twiloConfiguration;
        init();
    }

    private void init(){
        Twilio.init(
                twiloConfiguration.getSid(),
                twiloConfiguration.getToken()
        );
    }
}
