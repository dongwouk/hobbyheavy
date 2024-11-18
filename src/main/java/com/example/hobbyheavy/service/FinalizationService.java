package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
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

    /**
     * 특정 스케줄을 확정하는 메서드
     *
     * @param scheduleId 확정할 스케줄의 ID
     */
    @Transactional
    public void finalizeSchedule(Long scheduleId) {
        MeetupSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));

        // 스케줄 상태 검증
        validateScheduleStatus(schedule);

        // 스케줄 상태를 확정으로 변경
        confirmSchedule(schedule);
    }

    /**
     * 스케줄 상태를 확정으로 변경하는 메서드
     *
     * @param schedule 확정할 스케줄 객체
     */
    private void confirmSchedule(MeetupSchedule schedule) {
        schedule.setStatus(MeetupScheduleStatus.CONFIRMED);
        scheduleRepository.save(schedule);

        try {
            notificationService.notifyScheduleConfirmation(schedule);
            log.info("스케줄이 확정되고 알림이 전송되었습니다. ID: {}", schedule.getScheduleId());
        } catch (Exception e) {
            log.error("스케줄 확정 알림 전송 실패: 스케줄 ID: {}", schedule.getScheduleId(), e);
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
}
