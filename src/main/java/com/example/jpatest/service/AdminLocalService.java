package com.example.jpatest.service;

import com.example.jpatest.entity.AdminLocalEntity;
import com.example.jpatest.repository.AdminLocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminLocalService {

    private final AdminLocalRepository adminLocalRepository;

    @Autowired
    public AdminLocalService(AdminLocalRepository adminLocalRepository) {
        this.adminLocalRepository = adminLocalRepository;
    }

    // 로컬 정보 저장
    public AdminLocalEntity saveLocalInfo(AdminLocalEntity localEntity) {
        return adminLocalRepository.save(localEntity);
    }

    // ID로 로컬 정보 조회
    public AdminLocalEntity getLocalInfoById(Long id) {
        return adminLocalRepository.findById(id).orElse(null);
    }

    // 로컬 정보 삭제
    public void deleteLocalInfoById(Long id) {
        adminLocalRepository.deleteById(id);
    }

    // 기타 필요한 서비스 메서드 구현
}