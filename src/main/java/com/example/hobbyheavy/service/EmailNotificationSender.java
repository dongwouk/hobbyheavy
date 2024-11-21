package com.example.hobbyheavy.service;

import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 이메일 알림을 전송하는 구현체.
 * {@link NotificationSender}를 구현하여 이메일 전송 기능을 제공합니다.
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender javaMailSender;

    /**
     * 이메일을 전송합니다.
     * 기본적으로 "알림 메일"이라는 제목과 함께 메시지를 전송합니다.
     *
     * @param recipient 이메일 수신자
     * @param message   전송할 메시지 내용
     * @throws CustomException 이메일 전송 실패 시 예외 발생
     */
    @Override
    public void send(String recipient, String message) {
        sendWithSubject(recipient, "알림 메일", message);
    }

    /**
     * 이메일을 전송합니다.
     *
     * @param recipient 이메일 수신자
     * @param subject   이메일 제목
     * @param message   전송할 메시지 내용
     * @throws CustomException 이메일 전송 실패 시 예외 발생
     */
    public void sendWithSubject(String recipient, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipient);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            javaMailSender.send(mailMessage);
            log.info("[이메일 전송 성공] 수신자: {}, 제목: {}", recipient, subject);
        } catch (Exception e) {
            log.error("[이메일 전송 실패] 수신자: {}, 제목: {}", recipient, subject, e);
            handleEmailSendFailure(recipient, message);
            throw new CustomException(ExceptionCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 이메일 전송 실패 시 처리 메서드
     *
     * @param recipient 이메일 수신자
     * @param message   실패한 알림 메시지
     */
    private void handleEmailSendFailure(String recipient, String message) {
        log.error("[이메일 전송 실패 처리] 수신자: {}, 메시지: {}", recipient, message);
        // 여기에 재시도 로직이나 별도 알림 실패 처리 로직을 추가하는 것이 좋습니다.
    }
}
