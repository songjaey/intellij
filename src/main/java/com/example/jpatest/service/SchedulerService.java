package com.example.jpatest.service;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.Member;
import com.example.jpatest.entity.Scheduler;
import com.example.jpatest.repository.SchedulerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchedulerService {

    private final SchedulerRepository schedulerRepository;

    public void saveScheduler(SchedulerDto schedulerDto, Member member) {
        Scheduler scheduler = Scheduler.createScheduler(schedulerDto, member);
        schedulerRepository.save(scheduler);
    }
}