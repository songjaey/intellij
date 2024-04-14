package com.example.jpatest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerDto {
    private String departureHour;
    private String departureMinute;
    private String arrivalHour;
    private String arrivalMinute;
    private String trip_duration_start;
    private String trip_duration_end;
    private Long id; // 식별자(ID) 필드

    // Getter 및 Setter 메서드
    // 필요에 따라 다른 메서드들을 추가할 수 있습니다.
}
