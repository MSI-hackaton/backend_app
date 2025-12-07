package dev.msi_hackaton.backend_app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    private static final Logger log = LoggerFactory.getLogger(SMSService.class);

    public void sendVerificationCode(String phoneNumber) {
        log.info("SMS verification code sent to {}", phoneNumber);
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return "123456".equals(code);
    }
}