package com.example.jpatest.service;

import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.util.GeneticAlgorithmTSP;
import com.google.maps.model.DirectionsRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class GoogleMapsService {

    public DirectionsRoute[] getOptimalRoute(String originAdd, String destiAdd, List<AdminItemEntity> places) {
        return GeneticAlgorithmTSP.getOptimalRoute(originAdd, destiAdd, places);
    }
}