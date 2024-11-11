package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.exception.UnauthorizedException;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ParticipantRepository participantRepository;
    private final TaskScheduler taskScheduler;

    // 모임 스케줄 생성
    // 새로운 스케줄 추가 시 투표 마감 시간에 맞춘 동적 스케줄 생성
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        verifyHostOrSubHostRole(userId, scheduleRequest.getMeetupId());

        // Proposal Date 설정
        LocalDateTime proposalDate = scheduleRequest.getProposalDate();

        // 활동 시간 (activateTime) 설정 - 예상 소요 시간
        Duration activateDuration = parseDuration(scheduleRequest.getActivateTime());

        // 투표 마감 시간 (votingDeadline) 설정
        LocalDateTime votingDeadline;
        if (scheduleRequest.getVotingDeadline() == null) {
            votingDeadline = proposalDate.plusHours(3); // 기본값: 제안 시간으로부터 3시간 후
        } else {
            votingDeadline = proposalDate.plus(parseDuration(scheduleRequest.getVotingDeadline()));
        }

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(proposalDate)
                .activateTime(activateDuration) // 예상 소요 시간을 저장
                .votingDeadline(votingDeadline)
                .location(scheduleRequest.getLocation())
                .build();

        MeetupSchedule savedSchedule = scheduleRepository.save(meetupSchedule);

        // 동적 스케줄링 추가
        scheduleFinalization(savedSchedule);

        return ScheduleResponse.fromEntity(savedSchedule);
    }
    // 동적 스케줄링을 설정하는 메서드
    private void scheduleFinalization(MeetupSchedule schedule) {
        if (schedule.getVotingDeadline() != null) {
            LocalDateTime votingDeadline = schedule.getVotingDeadline();
            Date scheduledTime = Date.from(votingDeadline.atZone(ZoneId.systemDefault()).toInstant());

            taskScheduler.schedule(() -> finalizeSchedule(schedule.getScheduleId()), scheduledTime);
        }
    }

    // 특정 스케줄을 확정하는 메서드
    public void finalizeSchedule(Long scheduleId) {
        Optional<MeetupSchedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent() && optionalSchedule.get().getScheduleStatus() != MeetupScheduleStatus.CONFIRMED) {
            MeetupSchedule schedule = optionalSchedule.get();
            schedule.setStatus(MeetupScheduleStatus.CONFIRMED);
            scheduleRepository.save(schedule);
        }
    }

    // 모든 기존 스케줄에 대한 동적 스케줄링 설정 (애플리케이션 시작 시)
    @PostConstruct
    public void initializeDynamicSchedules() {
        List<MeetupSchedule> schedules = scheduleRepository.findAll();
        schedules.forEach(this::scheduleFinalization);
    }

    // 예상 소요 시간을 파싱하는 메서드
    public Duration parseDuration(String durationString) {
        Pattern pattern = Pattern.compile("(?:(\\d+)일)?\\s*(?:(\\d+)시간)?\\s*(?:(\\d+)분)?");
        Matcher matcher = pattern.matcher(durationString);

        int days = 0;
        int hours = 0;
        int minutes = 0;

        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                days = Integer.parseInt(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                hours = Integer.parseInt(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                minutes = Integer.parseInt(matcher.group(3));
            }
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
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

        // 참가자 조회
        Optional<Participant> optionalParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId);
        Participant participant;

        if (optionalParticipant.isEmpty()) {
            // 참가자가 없으면 자동으로 추가하고 투표 상태도 함께 설정
            User user = User.builder().userId(userId).build(); // 사용자를 생성하거나 조회하는 로직 필요
            participant = Participant.builder()
                    .user(user)
                    .meetup(meetupSchedule.getMeetup())
                    .meetupRole(ParticipantRole.MEMBER.name()) // 일반 참가자로 설정
                    .status(ParticipantStatus.APPROVED) // 기본 상태 설정
                    .hasVoted(true) // 투표 완료 상태로 설정
                    .build();

            participantRepository.save(participant);
        } else {
            // 기존 참가자라면 해당 객체 사용
            participant = optionalParticipant.get();

            // 이미 투표했는지 확인
            if (participant.getHasVoted()) {
                throw new RuntimeException("이미 투표하셨습니다.");
            }

            // 투표 완료 처리
            participant.setHasVoted(true);
            participantRepository.save(participant);
        }
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
        String role = participant.getMeetupRole();

        if (!(role.equals(ParticipantRole.HOST.name()) || role.equals(ParticipantRole.SUB_HOST.name()))) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }
}
