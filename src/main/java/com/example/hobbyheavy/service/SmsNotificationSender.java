package com.example.hobbyheavy.service;

import org.springframework.stereotype.Service;

@Service
public class SmsNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        // SMS 전송 로직 구현
        System.out.println("SMS sent to " + recipient + ": " + message);
    }
}
