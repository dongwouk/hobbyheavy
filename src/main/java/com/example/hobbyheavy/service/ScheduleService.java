package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.util.DurationParser;
import com.example.hobbyheavy.util.UserContextUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final AuthorizationService authorizationService;
    private final DynamicScheduleService dynamicScheduleService;

    /**
     * 새로운 모임 스케줄을 생성합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @return 생성된 스케줄의 응답 정보
     */
    @Transactional
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
        String userId = getCurrentUserId();
        verifyUserAuthorization(userId, scheduleRequest.getMeetupId());

        MeetupSchedule meetupSchedule = createMeetupSchedule(scheduleRequest);
        MeetupSchedule savedSchedule = scheduleRepository.save(meetupSchedule);

        addDynamicSchedule(savedSchedule);
        log.info("스케줄이 생성되었습니다. ID: {}", savedSchedule.getScheduleId());

        return ScheduleResponse.fromEntity(savedSchedule);
    }

    /**
     * 특정 모임 스케줄을 조회합니다.
     *
     * @param scheduleId 조회할 스케줄의 ID
     * @return 조회된 스케줄의 응답 정보
     */
    public ScheduleResponse getSchedule(Long scheduleId) {
        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);
        log.info("스케줄 조회: ID: {}", scheduleId);
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
     * @return 수정된 스케줄의 응답 정보
     */
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest) {
        String userId = getCurrentUserId();
        verifyUserAuthorization(userId, scheduleRequest.getMeetupId());

        MeetupSchedule existingSchedule = verifyAndGetSchedule(scheduleId);
        existingSchedule.updateFromDTO(scheduleRequest);
        MeetupSchedule updatedSchedule = scheduleRepository.save(existingSchedule);

        log.info("스케줄이 수정되었습니다. ID: {}", scheduleId);
        return ScheduleResponse.fromEntity(updatedSchedule);
    }

    /**
     * 특정 모임 스케줄을 삭제합니다.
     *
     * @param scheduleId 삭제할 스케줄의 ID
     */
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);
        String userId = getCurrentUserId();
        verifyUserAuthorization(userId, meetupSchedule.getMeetup().getMeetupId());

        try {
            scheduleRepository.delete(meetupSchedule);
            log.info("스케줄이 삭제되었습니다. ID: {}", scheduleId);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.SCHEDULE_DELETE_FAILED);
        }
    }

    /**
     * 특정 모임 스케줄을 취소합니다.
     *
     * @param scheduleId 취소할 스케줄의 ID
     * @param reason     취소 사유
     */
    @Transactional
    public void cancelSchedule(Long scheduleId, String reason) {
        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);
        String userId = getCurrentUserId();
        verifyUserAuthorization(userId, meetupSchedule.getMeetup().getMeetupId());

        if (meetupSchedule.getScheduleStatus() == MeetupScheduleStatus.CANCELLED) {
            throw new CustomException(ExceptionCode.SCHEDULE_CANCELLATION_NOT_ALLOWED);
        }

        meetupSchedule.setStatus(MeetupScheduleStatus.CANCELLED);
        meetupSchedule.setCancellationReason(reason);
        scheduleRepository.save(meetupSchedule);

        log.info("스케줄이 취소되었습니다. ID: {}, 이유: {}", scheduleId, reason);
    }

    /**
     * 스케줄을 조회하고 검증합니다.
     *
     * @param scheduleId 조회할 스케줄의 ID
     * @return 조회된 스케줄 객체
     * @throws ScheduleNotFoundException 스케줄이 존재하지 않을 경우 예외 발생
     */
    private MeetupSchedule verifyAndGetSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));
    }

    /**
     * 사용자의 권한을 검증합니다.
     *
     * @param userId   사용자 ID
     * @param meetupId 모임 ID
     */
    private void verifyUserAuthorization(String userId, Long meetupId) {
        try {
            // 권한을 가진 사용자(Participant) 정보를 조회
            authorizationService.verifyHostOrSubHostRole(userId, meetupId);
        } catch (Exception e) {
            // 권한이 없거나 조회에 실패한 경우 예외 발생
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }
    }

    /**
     * 현재 사용자 ID를 가져옵니다.
     *
     * @return 현재 사용자 ID
     */
    private String getCurrentUserId() {
        return UserContextUtil.getCurrentUserId();
    }

    /**
     * 주어진 요청 정보를 바탕으로 MeetupSchedule 객체를 생성합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @return 생성된 MeetupSchedule 객체
     */
    private MeetupSchedule createMeetupSchedule(ScheduleRequest scheduleRequest) {
        try {
            LocalDateTime proposalDate = scheduleRequest.getProposalDate();
            Duration activateDuration = DurationParser.parseDuration(scheduleRequest.getActivateTime());
            LocalDateTime votingDeadline = (scheduleRequest.getVotingDeadline() == null)
                    ? proposalDate.plusHours(3)
                    : proposalDate.plus(DurationParser.parseDuration(scheduleRequest.getVotingDeadline()));

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
     * 동적 스케줄링을 추가합니다.
     *
     * @param meetupSchedule 추가할 스케줄 객체
     */
    private void addDynamicSchedule(MeetupSchedule meetupSchedule) {
        dynamicScheduleService.scheduleFinalization(meetupSchedule);
        log.info("동적 스케줄링이 추가되었습니다. ID: {}", meetupSchedule.getScheduleId());
    }

}
