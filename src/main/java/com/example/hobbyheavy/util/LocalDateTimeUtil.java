package com.example.hobbyheavy.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class LocalDateTimeUtil {

    /** UtilityClass 로 static 키워드 추가 할 필요 없음 **/

    public String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); // LocalDateTime을 문자열로 변환
    }

    public LocalDateTime parseExpirationDate(String expirationDate) {
        return LocalDateTime.parse(expirationDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME); // 문자열을 LocalDateTime으로 변환
    }

}
