package com.example.jpatest.service;

import com.example.jpatest.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLocalService {

    @Autowired
    private final LocalRepository localRepository;

    public void deleteDetail(String country, String local){
        localRepository.deleteByCountryAndLocal(country, local);
    }
}
