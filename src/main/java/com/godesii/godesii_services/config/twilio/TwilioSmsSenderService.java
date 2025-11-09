package com.godesii.godesii_services.config.twilio;

import com.twilio.rest.api.v2010.account.Message;

public interface TwilioSmsSenderService {

    Message sendSms(SmsRequest smsRequest);
}
