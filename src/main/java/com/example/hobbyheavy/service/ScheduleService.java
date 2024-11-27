package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.NotificationMessage;
import com.example.hobbyheavy.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final MeetupRepository meetupRepository;
    private final ParticipantRepository participantRepository;

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

        // 모임이 존재하는지 검증
        if (!meetupRepository.existsById(scheduleRequest.getMeetupId())) {
            throw new CustomException(ExceptionCode.MEETUP_NOT_FOUND);
        }

        // 참가자의 역할 조회 및 검증
        Optional<Participant> participantOpt = participantRepository.findByMeetup_MeetupIdAndUser_UserId(scheduleRequest.getMeetupId(), userId);
        if (participantOpt.isEmpty() || (!participantOpt.get().getMeetupRole().equalsIgnoreCase("HOST") &&
                !participantOpt.get().getMeetupRole().equalsIgnoreCase("SUB_HOST"))) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }

        // 스케줄 제안 날짜 검증
        ScheduleUtils.validateProposalDate(scheduleRequest.getProposalDate());

        // 투표 마감 기한 계산
        LocalDateTime votingDeadline = ScheduleUtils.calculateVotingDeadline(scheduleRequest);

        // 투표 마감 기한 검증
        ScheduleUtils.validateVotingDeadline(votingDeadline);
        ScheduleUtils.validateVotingDeadlineWithProposal(votingDeadline, scheduleRequest.getProposalDate());

        // MeetupSchedule 객체 생성
        MeetupSchedule meetupSchedule = createMeetupSchedule(scheduleRequest);
        meetupSchedule.setVotingDeadline(votingDeadline);

        // 스케줄 저장
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
     * @param scheduleId     조회할 스케줄의 ID
     * @param notificationId 읽음 처리할 알림의 ID (옵션)
     * @return 조회된 스케줄의 응답 정보
     * @throws CustomException 스케줄이 존재하지 않을 경우 발생
     */
    public ScheduleResponse getSchedule(Long scheduleId, Long notificationId, String userId) {
        // 스케줄 조회 및 검증
        MeetupSchedule meetupSchedule = verifyAndGetActiveSchedule(scheduleId);
        log.info("스케줄 조회: ID: {}", scheduleId);

        // 사용자가 해당 스케줄의 모임에 속해 있는지 확인
        Long meetupId = meetupSchedule.getMeetup().getMeetupId();
        boolean isParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId).isPresent();
        if (!isParticipant) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }

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
    public List<ScheduleResponse> getAllSchedules(String userId) {
        // 사용자 참가 정보 조회
        List<Participant> participants = participantRepository.findAllByUser_UserId(userId);
        if (participants.isEmpty()) {
            log.info("User {}가 속한 모임이 없습니다.", userId);
            return Collections.emptyList(); // 사용자가 참가 중인 모임이 없으면 빈 리스트 반환
        }

        // 참가한 모임 ID 목록 생성
        List<Long> meetupIds = participants.stream()
                .map(participant -> participant.getMeetup().getMeetupId())
                .collect(Collectors.toList());

        // 사용자 참가 모임의 스케줄만 조회
        List<ScheduleResponse> schedules = scheduleRepository.findAllByMeetup_MeetupIdInAndDeletedFalse(meetupIds).stream()
                .map(ScheduleResponse::fromEntity)
                .collect(Collectors.toList());

        log.info("User {}가 속한 모든 스케줄 조회. 총 {}개의 스케줄이 있습니다.", userId, schedules.size());
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

        // 기존 스케줄 조회 및 검증
        MeetupSchedule existingSchedule = verifyAndGetSchedule(scheduleId);

        // 모임이 존재하는지 검증
        if (!meetupRepository.existsById(scheduleRequest.getMeetupId())) {
            throw new CustomException(ExceptionCode.MEETUP_NOT_FOUND);
        }

        // 참가자의 역할 조회 및 검증
        Optional<Participant> participantOpt = participantRepository.findByMeetup_MeetupIdAndUser_UserId(scheduleRequest.getMeetupId(), userId);
        if (participantOpt.isEmpty() || (!participantOpt.get().getMeetupRole().equalsIgnoreCase("HOST") &&
                !participantOpt.get().getMeetupRole().equalsIgnoreCase("SUB_HOST"))) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }

        // 스케줄 제안 날짜 검증
        ScheduleUtils.validateProposalDate(scheduleRequest.getProposalDate());

        // 투표 마감 기한 계산
        LocalDateTime votingDeadline = ScheduleUtils.calculateVotingDeadline(scheduleRequest);

        // 투표 마감 기한 검증
        ScheduleUtils.validateVotingDeadline(votingDeadline);
        ScheduleUtils.validateVotingDeadlineWithProposal(votingDeadline, scheduleRequest.getProposalDate());

        // 스케줄 수정 적용
        existingSchedule.updateFromDTO(scheduleRequest);
        existingSchedule.setVotingDeadline(votingDeadline);

        // 수정된 스케줄 저장
        MeetupSchedule updatedSchedule = scheduleRepository.save(existingSchedule);

        // 스케줄 수정 알림 발송
        notificationService.notifyParticipants(updatedSchedule, NotificationMessage.UPDATE);
        log.info("스케줄이 수정되고 알림이 전송되었습니다. ID: {}", scheduleId);

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

        // 기존 스케줄 조회 및 검증
        MeetupSchedule meetupSchedule = verifyAndGetSchedule(scheduleId);

        // 스케줄에 해당하는 모임 정보 가져오기
        Long meetupId = meetupSchedule.getMeetup().getMeetupId();

        // 참가자의 역할 조회 및 검증
        Optional<Participant> participantOpt = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);
        if (participantOpt.isEmpty() || !participantOpt.get().getMeetupRole().equalsIgnoreCase("HOST")) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }

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

        // 스케줄에 해당하는 모임 정보 가져오기
        Long meetupId = meetupSchedule.getMeetup().getMeetupId();

        // 참가자의 역할 조회 및 검증
        Optional<Participant> participantOpt = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);
        if (participantOpt.isEmpty() || !participantOpt.get().getMeetupRole().equalsIgnoreCase("HOST")) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }

        if (meetupSchedule.getScheduleStatus() == MeetupScheduleStatus.CANCELLED) {
            throw new CustomException(ExceptionCode.SCHEDULE_CANCELLATION_NOT_ALLOWED);
        }

        meetupSchedule.setStatus(MeetupScheduleStatus.CANCELLED);
        meetupSchedule.setCancellationReason(reason);
        scheduleRepository.save(meetupSchedule);

        // 스케줄 취소 알림 발송
        notificationService.notifyParticipants(meetupSchedule, NotificationMessage.CANCELLATION);

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

            LocalDateTime now = LocalDateTime.now();
            // 현재 시각보다 이후인지 검증
            if (proposalDate.isBefore(now)) {
                throw new CustomException(ExceptionCode.INVALID_PROPOSAL_DATE);
            }
            LocalDateTime votingDeadline = ScheduleUtils.calculateVotingDeadline(scheduleRequest);

            return MeetupSchedule.builder()
                    .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                    .proposalDate(proposalDate)
                    .activateTime(scheduleRequest.getActivateTime())
                    .votingDeadline(votingDeadline)
                    .location(scheduleRequest.getLocation())
                    .scheduleStatus(MeetupScheduleStatus.PROPOSED)
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
    private MeetupSchedule verifyAndGetActiveSchedule(Long scheduleId) {
        return scheduleRepository.findByScheduleIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));
    }
}
