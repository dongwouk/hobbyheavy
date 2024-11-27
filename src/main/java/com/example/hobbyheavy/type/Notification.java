package com.example.hobbyheavy.type;

import lombok.Getter;

@Getter
public enum Notification {
    NEW_MEETUP,       // 새로운 모임 생성
    NEW_JOIN,         // 새로운 참가자 참여
    SCHEDULE_UPDATE,  // 스케줄 업데이트
    SCHEDULE_CONFIRMATION, // 스케줄 확정
    SCHEDULE_CREATION, // 스케줄 생성
    CANCELLATION // 스케줄 취소
}
