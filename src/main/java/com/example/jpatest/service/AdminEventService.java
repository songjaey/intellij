package com.example.jpatest.service;

import com.example.jpatest.entity.AdminEventEntity;
import com.example.jpatest.repository.AdminEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminEventService {

    private final AdminEventRepository adminEventRepository;

    public List<AdminEventEntity> getAllEvents() {
        return adminEventRepository.findAll();
    }

    public void saveEvent(AdminEventEntity event) {
        adminEventRepository.save(event);
    }

    public void deleteEvent(Long id){
        adminEventRepository.deleteById(id);
    }
}
