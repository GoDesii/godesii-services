package com.godesii.godesii_services.config.twilio;

import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsSenderServiceImpl implements  TwilioSmsSenderService{

    public static final Logger LOGGER = LoggerFactory.getLogger(TwilioSmsSenderServiceImpl.class);

    private final TwilioConfig twilioConfiguration;

    public TwilioSmsSenderServiceImpl(TwilioConfig twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    @Override
    public Message sendSms(SmsRequest smsRequest) {
        Message  message = null;
        try {
//            if (isPhoneNoValid(smsRequest.getPhoneNo())) {
                message = Message.creator(
                        new PhoneNumber("+91"+smsRequest.getPhoneNo()), new PhoneNumber(twilioConfiguration.getPhoneNo()), smsRequest.getMessage()
                ).create();
//            }
        }
        catch (Exception e){
            LOGGER.error("Error in sending sms to phone no = {} with message {0}", smsRequest.getPhoneNo(), e);
        }
        return message;
    }

    private Boolean isPhoneNoValid(String phoneNo) {
//        phoneNo = phoneNo.replaceAll("[\\s()-]", "");
//        if ("".equals(phoneNo)) {
//            return false;
//        }
        try {
            com.twilio.rest.lookups.v1.PhoneNumber.fetcher(String.valueOf(new PhoneNumber(phoneNo))).fetch();
            return true;
        } catch (ApiException e) {
            if (e.getStatusCode() == 404) {
                return false;
            }
//            LOGGER.info("Phone number is not valid {}", phoneNo);
            throw new IllegalArgumentException("Phone number is not valid!");
        }
    }
}
