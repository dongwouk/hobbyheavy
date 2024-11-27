package com.example.hobbyheavy.util;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 스케줄 관련 유틸리티 메서드를 제공하는 클래스.
 */
@UtilityClass
public class ScheduleUtils {

    // 투표 제한 시간
    private static final int MAX_HOURS = 48;

    /**
     * 스케줄 제안 날짜가 현재 시간보다 이후인지 검증합니다.
     *
     * @param proposalDate 검증할 스케줄 제안 날짜
     * @throws CustomException 유효하지 않은 제안 날짜일 경우 발생
     */
    public void validateProposalDate(LocalDateTime proposalDate) {
        LocalDateTime now = LocalDateTime.now();

        // 스케줄 제안 날짜가 현재 시간 이전인지 검증
        if (proposalDate.isBefore(now)) {
            throw new CustomException(ExceptionCode.INVALID_PROPOSAL_DATE);
        }
    }

    /**
     * 스케줄의 투표 마감 기한을 계산합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 정보
     * @return 계산된 투표 마감 기한
     */
    public LocalDateTime calculateVotingDeadline(ScheduleRequest scheduleRequest) {
        LocalDateTime now = LocalDateTime.now();
        if (scheduleRequest.getVotingDeadline() == null) {
            // 투표 마감 기한이 없는 경우 현재 시간 기준으로 3시간 뒤로 설정
            return now.plusHours(3);
        } else {
            // 요청 데이터에서 투표 마감 기한을 설정
            Duration votingDuration = DurationParser.parseDuration(scheduleRequest.getVotingDeadline());
            return now.plus(votingDuration);
        }
    }

    /**
     * 투표 마감 기한이 현재 시간으로부터 최대 48시간을 초과하지 않는지 검증합니다.
     *
     * @param votingDeadline 검증할 투표 마감 기한
     * @throws CustomException 유효하지 않은 투표 마감 기한일 경우 발생
     */
    public void validateVotingDeadline(LocalDateTime votingDeadline) {

        LocalDateTime now = LocalDateTime.now();

        // 투표 마감 기한이 현재 시간 이전인지 검증
        if (votingDeadline.isBefore(now)) {
            throw new CustomException(ExceptionCode.INVALID_VOTING_DEADLINE);
        }

        // 투표 마감 기한이 최대 48시간을 초과하는지 검증
        if (votingDeadline.isAfter(now.plusHours(MAX_HOURS))) {
            throw new CustomException(ExceptionCode.INVALID_VOTING_DEADLINE);
        }
    }

    /**
     * 투표 마감 기한이 제안된 시간보다 늦게 끝나지 않도록 검증합니다.
     *
     * @param votingDeadline 투표 마감 기한
     * @param proposalDate   스케줄 제안 시간
     * @throws CustomException 유효하지 않은 투표 마감 기한일 경우 발생
     */
    public void validateVotingDeadlineWithProposal(LocalDateTime votingDeadline, LocalDateTime proposalDate) {
        // 투표 마감 기한이 제안된 시간보다 이후인지 검증
        if (votingDeadline.isAfter(proposalDate)) {
            throw new CustomException(ExceptionCode.INVALID_VOTING_DEADLINE);
        }
    }
}
