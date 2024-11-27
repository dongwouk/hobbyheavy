package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.NotificationResponse;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Notification;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.NotificationRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.type.NotificationMessage;
import com.example.hobbyheavy.type.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ParticipantRepository participantRepository;
    private final NotificationSender notificationSender;
    private final NotificationRepository notificationRepository; // 추가된 부분

    /**
     * 참여자들에게 스케줄 알림을 전송하는 메서드
     *
     * @param meetupSchedule 스케줄 객체
     * @param messageType    알림 메시지 타입(enum)
     */
    public void notifyParticipants(MeetupSchedule meetupSchedule, NotificationMessage messageType) {
        // 알림 설정이 활성화된 참여자만 필터링
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupSchedule.getMeetup().getMeetupId())
                .stream()
                .filter(Participant::getMeetupAlarm) // meetupAlarm이 true인 경우만 선택
                .collect(Collectors.toList());

        if (participants.isEmpty()) {
            log.warn("[알림 전송] 참여자가 없습니다. 스케줄 ID: {}", meetupSchedule.getScheduleId());
            throw new CustomException(ExceptionCode.NO_PARTICIPANTS);
        }

        for (Participant participant : participants) {
            String personalizedMessage = messageType.format(meetupSchedule.getScheduleId());
            sendNotification(participant, personalizedMessage);

            // NotificationType으로 변환
            NotificationType notificationType = NotificationType.valueOf(messageType.name());
            saveNotification(participant.getUser(), notificationType, personalizedMessage, meetupSchedule);
        }
    }

    /**
     * 참여자에게 알림을 전송하는 실제 메서드
     *
     * @param participant 참여자
     * @param message     전송할 메시지
     */
    @Async
    public void sendNotification(Participant participant, String message) {
        try {
            notificationSender.send(participant.getUser().getEmail(), message);
            log.info("[알림 전송] 성공 - 참여자: {}, 메시지: {}", participant.getUser().getUsername(), message);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.NOTIFICATION_SEND_FAILED, e);
        }
    }

    /**
     * 내부 알림 시스템을 위한 알림 저장 메서드
     *
     * @param user           알림 수신자
     * @param type           알림 유형
     * @param message        알림 메시지
     * @param meetupSchedule 관련된 스케줄 객체
     */
    private void saveNotification(User user, NotificationType type, String message, MeetupSchedule meetupSchedule) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)  // NotificationType 타입 사용
                .message(message)
                .meetup(meetupSchedule.getMeetup())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("[내부 알림 저장] 알림이 저장되었습니다. 사용자 ID: {}, 알림 유형: {}, 메시지: {}", user.getUserId(), type.name(), message);
    }

    /**
     * 특정 알림을 읽음 상태로 업데이트합니다.
     *
     * @param notificationId 읽음 처리할 알림의 ID
     * @throws CustomException 알림이 존재하지 않거나 업데이트가 실패할 경우 발생
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOTIFICATION_NOT_FOUND));

        if (notification.getIsRead()) {
            log.info("[알림 읽음 처리] 이미 읽은 알림입니다. 알림 ID: {}", notificationId);
            return;
        }

        notification.setIsRead(true);  // 읽음 상태로 업데이트
        notificationRepository.save(notification);
        log.info("[알림 읽음 처리] 알림이 읽음 처리되었습니다. 알림 ID: {}", notificationId);
    }

    public List<NotificationResponse> getUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findAllByUser_UserId(userId);
        return notifications.stream()
                .map(notification -> NotificationResponse.builder()
                        .notificationId(notification.getNotificationId())
                        .userId(notification.getUser().getUserId())
                        .message(notification.getMessage())
                        .type(notification.getType())
                        .isRead(notification.getIsRead())
                        .createdDate(notification.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }
}
