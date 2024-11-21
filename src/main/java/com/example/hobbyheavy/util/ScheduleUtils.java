package com.example.hobbyheavy.util;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 스케줄 관련 유틸리티 메서드를 제공하는 클래스.
 */
@UtilityClass
public class ScheduleUtils {

    /**
     * 스케줄의 투표 마감 기한을 계산합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @param proposalDate    제안된 날짜 및 시간
     * @return 계산된 투표 마감 기한
     */
    public LocalDateTime calculateVotingDeadline(ScheduleRequest scheduleRequest, LocalDateTime proposalDate) {
        if (scheduleRequest.getVotingDeadline() == null) {
            // 투표 마감 기한이 없는 경우 기본 3시간 뒤로 설정
            return proposalDate.plusHours(3);
        } else {
            // 요청 데이터에서 투표 마감 기한을 설정
            Duration votingDuration = DurationParser.parseDuration(scheduleRequest.getVotingDeadline());
            return proposalDate.plus(votingDuration);
        }
    }
}
