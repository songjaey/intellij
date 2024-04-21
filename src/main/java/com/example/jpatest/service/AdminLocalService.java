package com.example.jpatest.service;

import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLocalService {

    @Autowired
    private final LocalRepository localRepository;

    public void deleteDetail(String country, String local){
        localRepository.deleteByCountryAndLocal(country, local);
    }

    public LocalEntity findByLocalId(Long localId){
        Optional<LocalEntity> optionalLocalEntity = localRepository.findById(localId);
        if (optionalLocalEntity.isPresent()) {
            LocalEntity localEntity = optionalLocalEntity.get();
            return localEntity;
        }else {
            throw new RuntimeException("LocalEntity not found with id: " + localId);
        }
    }
}