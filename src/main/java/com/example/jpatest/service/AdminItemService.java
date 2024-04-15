package com.example.jpatest.service;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.repository.AdminItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminItemService {

    @Autowired
    private AdminItemRepository adminItemRepository;

    @Transactional
    public AdminItemEntity createAdminItem(AdminItemDto adminItemDto) {
        AdminItemEntity adminItemEntity = new AdminItemEntity();
        adminItemEntity.setTouristSpotName(adminItemDto.getTouristSpotName());
        adminItemEntity.setAddress(adminItemDto.getAddress());
        adminItemEntity.setContact(adminItemDto.getContact());
        adminItemEntity.setFeatures(adminItemDto.getFeatures());
        adminItemEntity.setBusinessHours(adminItemDto.getBusinessHours());

        // 이미지 경로 설정
        adminItemEntity.setImgUrl(adminItemDto.getImgUrl());

        return adminItemRepository.save(adminItemEntity);
    }
}