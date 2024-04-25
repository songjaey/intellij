package com.example.jpatest.service;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.Member;
import com.example.jpatest.repository.AdminItemRepository;
import com.example.jpatest.repository.MemberRepository;
import com.example.jpatest.util.GeneticAlgorithmTSP;
import com.google.maps.model.DirectionsRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleMapsService {

    private final AdminItemRepository adminItemRepository;
    public List<SchedulerDto> getOptimalRoute(String originAdd, String destiAdd, List<AdminItemEntity> places) {
        return GeneticAlgorithmTSP.getOptimalRoute(originAdd, destiAdd, places);
    }
    public AdminItemEntity getScheduler(Long id){
        Optional<AdminItemEntity> adminItemEntity = adminItemRepository.findById(id);
        SchedulerDto schedulerDto;
        if (adminItemEntity.isPresent()) {
            AdminItemEntity adminItemEntity1 = adminItemEntity.get();
            return adminItemEntity1;
        } else {
            return null;
        }
    }
}