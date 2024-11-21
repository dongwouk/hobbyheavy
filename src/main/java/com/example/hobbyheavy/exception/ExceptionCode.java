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
    USER_REGISTER_FAILED(HttpStatus.BAD_REQUEST, "회원가입 중 오류가 발생했습니다."),

    /** Token **/
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    /** Hobby **/
    HOBBY_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 취미 이름이 없습니다."),

    /** Meetup **/
    REMAIN_PARTICIPANTS(HttpStatus.CONFLICT, "모임에 참여자가 아직 남아있습니다."),
    MEETUP_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 모임을 찾을 수 없습니다."),
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색 키워드가 잘못되었습니다."),
    MEETUP_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "모임 삭제에 실패했습니다."),
    IMAGE_IO_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리 중 오류가 발생했습니다."),
    IMAGE_IS_EMPTY(HttpStatus.BAD_REQUEST, "이미지가 없습니다."),
    IMAGE_EXTENSION_MISMATCH(HttpStatus.BAD_REQUEST, "이미지 확장자가 아닙니다."),

    /** Participant **/
    NO_PARTICIPANTS(HttpStatus.NOT_FOUND, "참여자가 없습니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 대기자가 없습니다."),
    ALREADY_REQUEST(HttpStatus.CONFLICT, "이미 승인, 대기중인 참여자는 신청할 수 없습니다."),

    /** Comment **/
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 댓글을 찾을 수 없습니다."),
    COMMENT_USER_MISMATCH(HttpStatus.CONFLICT, "요청한 댓글의 작성자가 다릅니다."),

    /** Schedule **/
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 스케줄을 찾을 수 없습니다."),
    INVALID_SCHEDULE_DATE(HttpStatus.BAD_REQUEST, "스케줄 날짜가 유효하지 않습니다."),
    DUPLICATE_SCHEDULE(HttpStatus.CONFLICT, "중복된 스케줄이 이미 존재합니다."),
    SCHEDULE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄 수정에 실패하였습니다."),
    SCHEDULE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄 삭제에 실패하였습니다."),
    SCHEDULE_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄 생성에 실패하였습니다."),
    SCHEDULE_VOTING_NOT_ALLOWED(HttpStatus.FORBIDDEN, "해당 스케줄에 투표할 수 없습니다."),
    SCHEDULE_CANCELLATION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "해당 스케줄은 취소할 수 없습니다."),
    SCHEDULE_ALREADY_CONFIRMED(HttpStatus.CONFLICT, "이미 확정된 스케줄입니다."),
    SCHEDULE_CONFIRMATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "스케줄 확정에 실패하였습니다."),
    ALREADY_VOTED(HttpStatus.CONFLICT, "이미 투표하셨습니다."),

    /** Notification **/
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
