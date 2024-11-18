package com.example.hobbyheavy.repository;


import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<MeetupSchedule, Long> {
    /**
     * 특정 모임 ID로 스케줄 조회
     *
     * @param meetupId 모임 ID
     * @return 해당 모임의 스케줄 목록
     */
    List<MeetupSchedule> findByMeetupId(Long meetupId);

    /**
     * 특정 상태의 스케줄 조회
     *
     * @param scheduleStatus 스케줄 상태
     * @return 해당 상태의 스케줄 목록
     */
    List<MeetupSchedule> findByScheduleStatus(MeetupScheduleStatus scheduleStatus);

    /**
     * 특정 사용자가 포함된 스케줄 조회
     *
     * @param username 참여자의 사용자 이름
     * @return 해당 사용자가 포함된 스케줄 목록
     */
    List<MeetupSchedule> findByParticipantsUserUsername(String username);

    /**
     * 특정 모임 ID와 스케줄 상태로 조회
     *
     * @param meetupId       모임 ID
     * @param scheduleStatus 스케줄 상태
     * @return 해당 모임에서 특정 상태의 스케줄 목록
     */
    List<MeetupSchedule> findByMeetupIdAndScheduleStatus(Long meetupId, MeetupScheduleStatus scheduleStatus);

    /**
     * 특정 날짜 범위 내 스케줄 조회
     *
     * @param startDate 시작 날짜
     * @param endDate   종료 날짜
     * @return 해당 날짜 범위 내의 스케줄 목록
     */
    List<MeetupSchedule> findByProposalDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 모임 ID로 정렬된 스케줄 조회
     *
     * @param meetupId 모임 ID
     * @return 정렬된 스케줄 목록
     */
    List<MeetupSchedule> findByMeetupIdOrderByProposalDateAsc(Long meetupId);
}
