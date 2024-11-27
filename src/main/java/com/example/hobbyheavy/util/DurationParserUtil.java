package com.example.hobbyheavy.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열로 표현된 기간(duration)을 {@link Duration} 객체로 변환하는 유틸리티 클래스.
 * <p>
 * "일", "시간", "분" 형식의 문자열을 파싱하여 {@link Duration}으로 반환합니다.
 * </p>
 */
@UtilityClass
public class DurationParserUtil {

    /**
     * 문자열로 표현된 기간을 파싱하여 {@link Duration} 객체로 변환합니다.
     * <p>
     * 지원하는 입력 형식:
     * - "2일 3시간 15분"
     * - "1시간 45분"
     * - "30분"
     * </p>
     * <p>
     * 입력 형식이 올바르지 않을 경우 기본 값 0일, 0시간, 0분이 반환됩니다.
     * </p>
     *
     * @param durationString 파싱할 기간 문자열 (예: "2일 3시간 15분")
     * @return 변환된 {@link Duration} 객체
     * @throws NumberFormatException 숫자 파싱에 실패할 경우 발생
     */
    public Duration parseDuration(String durationString) {
        Pattern pattern = Pattern.compile("(?:(\\d+)일)?\\s*(?:(\\d+)시간)?\\s*(?:(\\d+)분)?");
        Matcher matcher = pattern.matcher(durationString);

        int days = 0;
        int hours = 0;
        int minutes = 0;

        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                days = Integer.parseInt(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                hours = Integer.parseInt(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                minutes = Integer.parseInt(matcher.group(3));
            }
        }

        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }
}
