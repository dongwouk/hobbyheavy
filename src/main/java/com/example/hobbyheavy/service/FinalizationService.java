package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.NotificationMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * 모임 스케줄 확정 서비스
 * 일정 확정 로직 구현
 * 투표 결과를 바탕으로 최종 스케줄 설정
 */
public class FinalizationService {
    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;
    private final ParticipantRepository participantRepository;

    /**
     * 특정 스케줄을 확정하는 메서드
     *
     * @param scheduleId 확정할 스케줄의 ID
     * @param userId     확정을 요청한 사용자 ID (자동 확정 시에는 null)
     */
    @Transactional
    public void finalizeSchedule(Long scheduleId, String userId) {
        MeetupSchedule schedule = getSchedule(scheduleId);

        validateScheduleStatus(schedule);
        if (userId != null) {
            verifyUserAuthorization(userId, schedule);
        }


        confirmSchedule(schedule);
    }

    /**
     * 스케줄을 조회하는 메서드
     *
     * @param scheduleId 조회할 스케줄의 ID
     * @return 조회된 스케줄 객체
     */
    private MeetupSchedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));
    }

    /**
     * 사용자의 권한을 검증하는 메서드
     *
     * @param userId   사용자 ID
     * @param schedule 스케줄 객체
     */
    private void verifyUserAuthorization(String userId, MeetupSchedule schedule) {
        Participant participant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(schedule.getMeetup().getMeetupId(), userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.FORBIDDEN_ACTION));

        if (!participant.getMeetupRole().equals("HOST") && !participant.getMeetupRole().equals("SUB_HOST")) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }
    }

    /**
     * 스케줄 상태를 검증하는 메서드
     *
     * @param schedule 검증할 스케줄 객체
     */
    private void validateScheduleStatus(MeetupSchedule schedule) {
        if (schedule.getScheduleStatus() == MeetupScheduleStatus.CONFIRMED) {
            throw new CustomException(ExceptionCode.SCHEDULE_ALREADY_CONFIRMED);
        }
    }

    /**
     * 스케줄 상태를 확정으로 변경하는 메서드
     *
     * @param schedule 확정할 스케줄 객체
     */
    private void confirmSchedule(MeetupSchedule schedule) {
        schedule.setStatus(MeetupScheduleStatus.CONFIRMED);
        scheduleRepository.save(schedule);

        // 알림 전송
        notificationService.notifyParticipants(schedule, NotificationMessage.CONFIRMATION);
        log.info("스케줄이 확정되고 알림이 전송되었습니다. ID: {}, 상태: {}", schedule.getScheduleId(), schedule.getScheduleStatus());
    }
}
