package com.example.jpatest.service;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.repository.AdminItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminItemService {
    private final AdminItemRepository adminItemRepository;

    @Autowired
    public AdminItemService(AdminItemRepository adminItemRepository) {
        this.adminItemRepository = adminItemRepository;
    }

    @Transactional
    public void saveAdminItem(AdminItemDto adminItemDto) {
        // AdminItemDto를 AdminItemEntity로 변환
        AdminItemEntity adminItemEntity = convertToEntity(adminItemDto);

        // AdminItemEntity를 저장
        adminItemRepository.save(adminItemEntity);
    }

    private AdminItemEntity convertToEntity(AdminItemDto adminItemDto) {
        AdminItemEntity adminItemEntity = new AdminItemEntity();
        adminItemEntity.setImgUrl(adminItemDto.getImgUrl()); // 이미지 URL 설정
        adminItemEntity.setTouristSpotName(adminItemDto.getTouristSpotName());
        adminItemEntity.setAddress(adminItemDto.getAddress());
        adminItemEntity.setContact(adminItemDto.getContact());
        adminItemEntity.setFeatures(adminItemDto.getFeatures());
        adminItemEntity.setBusinessHours(adminItemDto.getBusinessHours());
        // 필요한 다른 필드들도 설정

        return adminItemEntity;
    }
}