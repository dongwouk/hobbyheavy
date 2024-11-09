package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.exception.UnauthorizedException;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.ParticipantRole;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ParticipantRepository participantRepository;

    // 모임 스케줄 생성
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        verifyHostOrSubHostRole(userId, scheduleRequest.getMeetupId());

        MeetupSchedule meetupSchedule = scheduleRequest.toEntity(scheduleRequest);
        MeetupSchedule savedSchedule = scheduleRepository.save(meetupSchedule);
        return ScheduleResponse.fromEntity(savedSchedule);
    }

    // 특정 모임 스케줄 조회
    public ScheduleResponse getSchedule(Long scheduleId) {
        MeetupSchedule meetupSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        return ScheduleResponse.fromEntity(meetupSchedule);
    }

    // 모든 모임 스케줄 조회
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(ScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 모임 스케줄 수정
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleRequest scheduleRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        verifyHostOrSubHostRole(userId, scheduleRequest.getMeetupId());

        MeetupSchedule existingSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));

        existingSchedule.updateFromDTO(scheduleRequest);
        MeetupSchedule updatedSchedule = scheduleRepository.save(existingSchedule);
        return ScheduleResponse.fromEntity(updatedSchedule);
    }


    // 모임 스케줄 삭제
    public void deleteSchedule(Long scheduleId) {
        MeetupSchedule meetupSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        verifyHostOrSubHostRole(userId, meetupSchedule.getMeetup().getMeetupId());

        scheduleRepository.delete(meetupSchedule);
    }

    // 일정에 투표하기
    public void voteOnSchedule(Long scheduleId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        MeetupSchedule meetupSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));

        // 해당 모임에 참가자인지 확인
        Optional<Participant> optionalParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId);
        if (optionalParticipant.isEmpty()) {
            throw new UnauthorizedException("해당 모임의 참가자가 아닙니다.");
        }

        Participant participant = optionalParticipant.get();
        if (participant.getHasVoted()) {
            throw new RuntimeException("이미 투표하셨습니다.");
        }

        participant.setHasVoted(true);
        participantRepository.save(participant);
    }

    // 투표 마감일 처리 (스케줄 확정)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void finalizeSchedules() {
        List<MeetupSchedule> schedules = scheduleRepository.findAll();
        LocalDate today = LocalDate.now();

        schedules.stream()
                .filter(schedule -> schedule.getVotingDeadline() != null && schedule.getScheduleStatus() != MeetupScheduleStatus.CONFIRMED)
                .filter(schedule -> schedule.getVotingDeadline().isBefore(today) || schedule.getVotingDeadline().isEqual(today))
                .forEach(schedule -> {
                    schedule.setStatus(MeetupScheduleStatus.CONFIRMED);
                    scheduleRepository.save(schedule);
                });
    }

    // 일정 취소
    public void cancelSchedule(Long scheduleId, String reason) {
        MeetupSchedule meetupSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        verifyHostOrSubHostRole(userId, meetupSchedule.getMeetup().getMeetupId());

        meetupSchedule.setStatus(MeetupScheduleStatus.CANCELLED);
        meetupSchedule.setCancellationReason(reason);
        scheduleRepository.save(meetupSchedule);
    }

    // 호스트 또는 부호스트 권한 확인 메서드
    private void verifyHostOrSubHostRole(String userId, Long meetupId) {
        Optional<Participant> optionalParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);

        if (optionalParticipant.isEmpty()) {
            throw new UnauthorizedException("권한이 없습니다.");
        }

        Participant participant = optionalParticipant.get();
        String roleString = participant.getMeetupRole(); // 예: "MEMBER,SUB_HOST"

        // 쉼표로 역할을 분리하여 리스트로 만듦
        List<String> roles = List.of(roleString.split(","));

        // 역할 리스트에 HOST 또는 SUB_HOST가 있는지 확인
        boolean hasRequiredRole = roles.stream()
                .map(String::trim) // 각 역할의 공백을 제거
                .anyMatch(role -> role.equals(ParticipantRole.HOST.name()) || role.equals(ParticipantRole.SUB_HOST.name()));

        if (!hasRequiredRole) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }
}
