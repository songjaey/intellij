package com.example.jpatest.entity;

import lombok.Getter;
import lombok.Setter;
import com.example.jpatest.dto.SchedulerDto;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 식별자 (primary key)

    @Column
    private String departureHour;
    @Column
    private String departureMinute;
    @Column
    private String arrivalHour;
    @Column
    private String arrivalMinute;
    @Column
    private String trip_duration_start;
    @Column
    private String trip_duration_end;
    @ElementCollection
    private List<Long> localIds;
    @ElementCollection
    private List<Long> adminItemIds;

    public static Scheduler createScheduler(SchedulerDto schedulerDto) {
        Scheduler scheduler = new Scheduler();
        scheduler.setArrivalHour(schedulerDto.getArrivalHour());
        scheduler.setArrivalMinute(schedulerDto.getArrivalMinute());
        scheduler.setDepartureHour(schedulerDto.getDepartureHour());
        scheduler.setDepartureMinute(schedulerDto.getDepartureMinute());
        scheduler.setTrip_duration_start(schedulerDto.getTrip_duration_start());
        scheduler.setTrip_duration_end(schedulerDto.getTrip_duration_end());
        return scheduler;
    }
}