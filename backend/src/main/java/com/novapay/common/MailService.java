package com.novapay.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    /**
     * برای dev: لینک رو در کنسول چاپ می‌کنیم.
     * برای prod: SMTP/JavaMailSender اضافه می‌شه.
     */
    public void sendPasswordResetLink(String email, String resetLink) {
        log.info("📧 PASSWORD RESET for {} → {}", email, resetLink);
    }
}