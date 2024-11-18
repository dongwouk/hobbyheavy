package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.type.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ParticipantRepository participantRepository;
    private final NotificationSender notificationSender;

    /**
     * 참여자들에게 스케줄 알림을 전송하는 메서드
     *
     * @param meetupSchedule 스케줄 객체
     * @param messageType    알림 메시지 타입(enum)
     */
    public void notifyParticipants(MeetupSchedule meetupSchedule, NotificationMessage messageType) {
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupSchedule.getMeetup().getMeetupId());

        if (participants.isEmpty()) {
            log.warn("[알림 전송] 참여자가 없습니다. 스케줄 ID: {}", meetupSchedule.getScheduleId());
            throw new CustomException(ExceptionCode.NO_PARTICIPANTS);
        }

        for (Participant participant : participants) {
            String personalizedMessage = messageType.format(meetupSchedule.getScheduleId());
            sendNotification(participant, personalizedMessage);
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
            log.error("[알림 전송] 실패 - 참여자: {}, 메시지: {}", participant.getUser().getUsername(), message, e);
            handleAsyncError(participant, message, e);
        }
    }

    private void handleAsyncError(Participant participant, String message, Exception e) {
        log.error("[비동기 알림 전송] 실패 처리 - 참여자: {}, 메시지: {}", participant.getUser().getUsername(), message, e);
        // 재시도 로직 추가 가능
    }

    public void notifyScheduleConfirmation(MeetupSchedule meetupSchedule) {
        notifyParticipants(meetupSchedule, NotificationMessage.CONFIRMATION);
    }

}
