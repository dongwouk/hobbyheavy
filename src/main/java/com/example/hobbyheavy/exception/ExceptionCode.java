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
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    USER_ID_ALREADY_IN_USE(HttpStatus.CONFLICT, "이미 사용중인 아이디 입니다."),
    EMAIL_ALREADY_IN_USE(HttpStatus.CONFLICT, "이미 사용중인 이메일 입니다."),
    USER_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴에 실패 하였습니다."),

    /** Token **/
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

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
