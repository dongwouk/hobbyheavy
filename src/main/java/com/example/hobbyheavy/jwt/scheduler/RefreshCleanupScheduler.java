package com.example.hobbyheavy.jwt.scheduler;

import com.example.hobbyheavy.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefreshCleanupScheduler {

    private final RefreshRepository refreshRepository;

    // 하루에 한번, 매일 자정에 실행 (초, 분, 시, 일, 월, 요일)
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();

        // 만료된 토큰 삭제
        refreshRepository.deleteAllByExpirationBefore(now);
    }
}