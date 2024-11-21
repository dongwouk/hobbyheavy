package com.example.hobbyheavy.type;

import lombok.Getter;

/**
 * 알림 메시지 템플릿을 정의하는 열거형(enum).
 * <p>
 * 각 열거형 상수는 메시지 템플릿을 포함하며, 이를 기반으로 알림 메시지를 동적으로 생성할 수 있습니다.
 * </p>
 */
@Getter
public enum NotificationMessage {

    SCHEDULE_CREATION("새로운 스케줄이 생성되었습니다. 스케줄 ID: %d"),
    CONFIRMATION("스케줄이 확정되었습니다. 스케줄 ID: %s"),
    CANCELLATION("스케줄이 취소되었습니다. 스케줄 ID: %s");

    private final String template;

    /**
     * 알림 메시지 템플릿을 초기화합니다.
     *
     * @param template 메시지 템플릿
     */
    NotificationMessage(String template) {
        this.template = template;
    }

    /**
     * 알림 메시지 템플릿을 반환합니다.
     *
     * @return 메시지 템플릿
     */
    public String getTemplate() {
        return template;
    }

    /**
     * 메시지 템플릿에 동적으로 데이터를 삽입하여 완전한 메시지를 생성합니다.
     *
     * @param args 템플릿에 삽입할 데이터
     * @return 완성된 메시지
     */
    public String format(Object... args) {
        return String.format(template, args);
    }
}
