package com.example.hobbyheavy.service;

/**
 * 알림 전송 인터페이스
 * 이메일, SMS, 푸시 알림 등 다양한 방식의 알림 전송을 지원
 */
public interface NotificationSender {

    /**
     * 알림 전송 메서드
     *
     * @param recipient 수신자 (이메일 주소, 전화번호 등)
     * @param message   전송할 메시지
     */
    void send(String recipient, String message);
}
