package com.example.hobbyheavy.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    /** [Exception Code]
     * UNAUTHORIZED : 401
     * FORBIDDEN : 403
     * CONFLICT : 409
     **/

    /** User **/
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증된 사용자가 아닙니다."),
    FORBIDDEN_ACTION(HttpStatus.FORBIDDEN, "요청하신 작업을 수행할 권한이 없습니다."),

    /** Hobby **/
    HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 취미 이름이 없습니다."),

    /** Meetup **/
    REMAIN_PARTICIPANTS(HttpStatus.CONFLICT, "모임에 참여자가 아직 남아있습니다."),
    MEETUP_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 모임을 찾을 수 없습니다."),

    /** Participant **/
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 대기자가 없습니다."),
    ALREADY_REQUEST(HttpStatus.CONFLICT, "이미 승인, 대기중인 참여자는 신청할 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
