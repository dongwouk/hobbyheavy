package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import org.springframework.stereotype.Service;

@Service
public class SharingService {
    public void shareSchedule(MeetupSchedule meetupSchedule) {
        System.out.println("일정 공유 작업 수행 중...");
    }
}
