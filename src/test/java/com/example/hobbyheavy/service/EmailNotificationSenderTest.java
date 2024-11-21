package com.example.hobbyheavy.service;

import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailNotificationSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailNotificationSender emailNotificationSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendEmailSuccess() {
        // given
        String recipient = "test@example.com";
        String message = "This is a test message";

        // when
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // then
        emailNotificationSender.send(recipient, message);

        // verify
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmailWithSubjectSuccess() {
        // given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String message = "This is a test message";

        // when
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // then
        emailNotificationSender.sendWithSubject(recipient, subject, message);

        // verify
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendEmailFailure() {
        // given
        String recipient = "test@example.com";
        String message = "This is a test message";

        // when
        doThrow(new RuntimeException("Email sending failed")).when(javaMailSender).send(any(SimpleMailMessage.class));

        // then
        assertThatThrownBy(() -> emailNotificationSender.send(recipient, message))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("exceptionCode", ExceptionCode.EMAIL_SEND_FAILED);

        // verify
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
