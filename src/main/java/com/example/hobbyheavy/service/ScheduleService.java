package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.NotificationMessage;
import com.example.hobbyheavy.util.DurationParser;
import com.example.hobbyheavy.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 스케줄과 관련된 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final DynamicScheduleService dynamicScheduleService;
    private final NotificationService notificationService;

    /**
     * 새로운 모임 스케줄을 생성합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @param userId          사용자 ID
     * @return 생성된 스케줄의 응답 정보
     * @throws CustomException 스케줄 생성에 실패한 경우 발생
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest, String userId) {
        log.info("스케줄 생성 요청 - 사용자 ID: {}, 요청 데이터: {}", userId, scheduleRequest);

        MeetupSchedule meetupSchedule = createMeetupSchedule(scheduleRequest);
        MeetupSchedule savedSchedule = scheduleRepository.save(meetupSchedule);

        dynamicScheduleService.scheduleFinalization(savedSchedule);
        log.info("스케줄이 생성되었습니다. ID: {}", savedSchedule.getScheduleId());

        notificationService.notifyParticipants(savedSchedule, NotificationMessage.SCHEDULE_CREATION);
        log.info("스케줄 생성 알림이 발송되었습니다. 스케줄 ID: {}", savedSchedule.getScheduleId());


        return ScheduleResponse.fromEntity(savedSchedule);
    }

    /**
     * 특정 모임 스케줄을 조회하고 관련 알림을 읽음 처리합니다.
     *
     * @param scheduleId    조회할 스케줄의 ID
     * @param notificationId 읽음 처리할 알림의 ID (옵션)
     * @return 조회된 스케줄의 응답 정보
     * @throws CustomException 스케줄이 존재하지 않을 경우 발생
     */
    public ScheduleResponse getSchedule(Long scheduleId, Long notificationId) {
        // 스케줄 조회 및 검증
        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);
        log.info("스케줄 조회: ID: {}", scheduleId);

        // 알림 ID가 제공된 경우 해당 알림 읽음 처리
        if (notificationId != null) {
            try {
                notificationService.markAsRead(notificationId);
                log.info("알림이 읽음 처리되었습니다. 알림 ID: {}", notificationId);
            } catch (CustomException e) {
                log.error("알림 읽음 처리 중 오류 발생: {}", e.getMessage());
            }
        }
        // 스케줄 응답 생성 및 반환
        return ScheduleResponse.fromEntity(meetupSchedule);
    }


    /**
     * 모든 모임 스케줄을 조회합니다.
     *
     * @return 모든 스케줄의 응답 정보 리스트
     */
    public List<ScheduleResponse> getAllSchedules() {
        List<ScheduleResponse> schedules = scheduleRepository.findAll().stream()
                .map(ScheduleResponse::fromEntity)
                .collect(Collectors.toList());
        log.info("모든 스케줄 조회. 총 {}개의 스케줄이 있습니다.", schedules.size());
        return schedules;
    }

    /**
     * 특정 모임 스케줄을 수정합니다.
     *
     * @param scheduleId      수정할 스케줄의 ID
     * @param scheduleRequest 수정할 스케줄의 요청 정보
     * @param userId          사용자 ID
     * @return 수정된 스케줄의 응답 정보
     * @throws CustomException 스케줄 수정에 실패한 경우 발생
     */
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest, String userId) {
        log.info("스케줄 수정 요청 - 스케줄 ID: {}, 사용자 ID: {}, 요청 데이터: {}", scheduleId, userId, scheduleRequest);

        MeetupSchedule existingSchedule = verifyAndGetSchedule(scheduleId);
        existingSchedule.updateFromDTO(scheduleRequest);
        MeetupSchedule updatedSchedule = scheduleRepository.save(existingSchedule);

        log.info("스케줄이 수정되었습니다. ID: {}", scheduleId);
        return ScheduleResponse.fromEntity(updatedSchedule);
    }

    /**
     * 특정 모임 스케줄을 삭제합니다. 논리적 삭제 방식 적용.
     *
     * @param scheduleId 삭제할 스케줄의 ID
     * @param userId     사용자 ID
     * @throws CustomException 스케줄 삭제에 실패한 경우 발생
     */
    @Transactional
    public void deleteSchedule(Long scheduleId, String userId) {
        log.info("스케줄 삭제 요청 - 스케줄 ID: {}, 사용자 ID: {}", scheduleId, userId);

        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);
        meetupSchedule.markAsDeleted();
        scheduleRepository.save(meetupSchedule);
        log.info("스케줄이 논리적으로 삭제되었습니다. ID: {}", scheduleId);
    }

    /**
     * 특정 모임 스케줄을 취소합니다.
     *
     * @param scheduleId 취소할 스케줄의 ID
     * @param reason     취소 사유
     * @param userId     사용자 ID
     * @throws CustomException 스케줄 취소에 실패한 경우 발생
     */
    @Transactional
    public void cancelSchedule(Long scheduleId, String reason, String userId) {
        log.info("스케줄 취소 요청 - 스케줄 ID: {}, 사용자 ID: {}, 이유: {}", scheduleId, userId, reason);

        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);

        if (meetupSchedule.getScheduleStatus() == MeetupScheduleStatus.CANCELLED) {
            throw new CustomException(ExceptionCode.SCHEDULE_CANCELLATION_NOT_ALLOWED);
        }

        meetupSchedule.setStatus(MeetupScheduleStatus.CANCELLED);
        meetupSchedule.setCancellationReason(reason);
        scheduleRepository.save(meetupSchedule);

        log.info("스케줄이 취소되었습니다. ID: {}, 이유: {}", scheduleId, reason);
    }

    /**
     * 주어진 요청 정보를 바탕으로 MeetupSchedule 객체를 생성합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @return 생성된 MeetupSchedule 객체
     * @throws CustomException 스케줄 생성에 실패한 경우 발생
     */
    private MeetupSchedule createMeetupSchedule(ScheduleRequest scheduleRequest) {
        try {
            LocalDateTime proposalDate = scheduleRequest.getProposalDate();
            Duration activateDuration = DurationParser.parseDuration(scheduleRequest.getActivateTime());
            LocalDateTime votingDeadline = ScheduleUtils.calculateVotingDeadline(scheduleRequest, proposalDate);

            return MeetupSchedule.builder()
                    .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                    .proposalDate(proposalDate)
                    .activateTime(activateDuration)
                    .votingDeadline(votingDeadline)
                    .location(scheduleRequest.getLocation())
                    .build();
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.SCHEDULE_CREATION_FAILED);
        }
    }

    /**
     * 스케줄을 조회하고 검증합니다.
     *
     * @param scheduleId 조회할 스케줄의 ID
     * @return 조회된 스케줄 객체
     * @throws CustomException 스케줄이 존재하지 않을 경우 발생
     */
    private MeetupSchedule verifyAndGetSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));
    }
}
