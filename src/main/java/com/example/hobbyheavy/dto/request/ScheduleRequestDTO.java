package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.Meetups;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDTO {

    private Meetups meetup;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
