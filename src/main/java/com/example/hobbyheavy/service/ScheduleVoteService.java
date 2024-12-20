package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 스케줄 투표와 관련된 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class ScheduleVoteService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public void voteOnSchedule(Long scheduleId, String userId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));

        // 이미 해당 사용자가 투표했는지 확인
        if (schedule.getVotes().contains(userId)) {
            throw new CustomException(ExceptionCode.ALREADY_VOTED);
        }

        schedule.addVote(userId);
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void removeVoteOnSchedule(Long scheduleId, String userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));

        // 해당 사용자가 투표한 기록이 있는지 확인
        if (!schedule.getVotes().contains(userId)) {
            throw new CustomException(ExceptionCode.VOTE_NOT_FOUND);
        }

        // 투표 삭제
        schedule.removeVote(userId);
        scheduleRepository.save(schedule);
    }
}


