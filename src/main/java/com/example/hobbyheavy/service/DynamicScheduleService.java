package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TaskScheduler taskScheduler;
    private final FinalizationService finalizationService;

    /**
     * 동적 스케줄링을 설정하는 메서드
     *
     * @param schedule 확정할 스케줄 객체
     */
    public void scheduleFinalization(MeetupSchedule schedule) {
        if (schedule.getScheduleStatus() == MeetupScheduleStatus.CONFIRMED) {
            log.info("확정된 스케줄은 스케줄링을 건너뜁니다. 스케줄 ID: {}", schedule.getScheduleId());
            return;
        }

        if (schedule.getVotingDeadline() != null) {
            LocalDateTime votingDeadline = schedule.getVotingDeadline();
            Date scheduledTime = Date.from(votingDeadline.atZone(ZoneId.systemDefault()).toInstant());

            taskScheduler.schedule(() -> {
                try {
                    finalizationService.finalizeSchedule(schedule.getScheduleId(), null); // 유저 ID가 필요 없는 자동 확정
                } catch (CustomException e) {
                    log.error("스케줄 확정 중 오류가 발생했습니다. 스케줄 ID: {}, 오류: {}", schedule.getScheduleId(), e.getMessage(), e);
                }
            }, scheduledTime);

            log.info("스케줄링이 설정되었습니다. 스케줄 ID: {}, 마감 시간: {}", schedule.getScheduleId(), votingDeadline);
        } else {
            log.warn("스케줄에 투표 마감 기한이 없습니다. 스케줄링이 설정되지 않았습니다. 스케줄 ID: {}", schedule.getScheduleId());
        }
    }

    /**
     * 모든 기존 스케줄에 대한 동적 스케줄링 설정 (애플리케이션 시작 시)
     */
    @PostConstruct
    public void initializeDynamicSchedules() {
        List<MeetupSchedule> schedules = scheduleRepository.findAll();
        schedules.forEach(schedule -> {
            if (schedule.getVotingDeadline() != null && schedule.getVotingDeadline().isAfter(LocalDateTime.now())) {
                scheduleFinalization(schedule);
            } else {
                log.info("마감 시간이 지난 스케줄은 초기화에서 제외됩니다. 스케줄 ID: {}", schedule.getScheduleId());
            }
        });
    }
}
