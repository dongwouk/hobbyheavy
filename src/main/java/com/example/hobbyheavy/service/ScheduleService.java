package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    // 모임 스케줄 생성
    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
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
        scheduleRepository.delete(meetupSchedule);
    }
}
