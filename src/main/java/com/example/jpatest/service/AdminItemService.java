package com.example.jpatest.service;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.AdminItemRepository;
import com.example.jpatest.repository.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminItemService {

    private final AdminItemRepository adminItemRepository;
    private final LocalRepository localRepository;

    @Autowired
    public AdminItemService(AdminItemRepository adminItemRepository, LocalRepository localRepository) {
        this.adminItemRepository = adminItemRepository;
        this.localRepository = localRepository;
    }

    // Method to find all AdminItemEntity objects
    public List<AdminItemEntity> findAll() {
        return adminItemRepository.findAll();
    }

    @Transactional
    public void saveAdminItem(AdminItemDto adminItemDto, Long localId) {
        Optional<LocalEntity> optionalLocalEntity = localRepository.findById(localId);
        if (optionalLocalEntity.isPresent()) {
            LocalEntity localEntity = optionalLocalEntity.get();

            AdminItemEntity adminItemEntity = convertToEntity(adminItemDto);
            adminItemEntity.setLocal(localEntity); // Set localEntity directly

            adminItemRepository.save(adminItemEntity);
        } else {
            throw new RuntimeException("LocalEntity not found with id: " + localId);
        }
    }

    public void deleteItem(Long id) {
        adminItemRepository.deleteById(id);
    }

    public AdminItemEntity findById(Long itemId) {
        // itemId를 사용하여 데이터베이스에서 아이템 정보를 조회
        return adminItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    }
    public List<AdminItemEntity> findBylistId(Long localId) {
        // localId를 사용하여 데이터베이스에서 아이템 정보를 조회
        return adminItemRepository.findByLocalId(localId);
    }

    public List<AdminItemEntity> findByIds(List<Long> localIdList){
        return adminItemRepository.findByIds(localIdList);
    }


    private AdminItemEntity convertToEntity(AdminItemDto adminItemDto) {
        AdminItemEntity adminItemEntity = new AdminItemEntity();
        adminItemEntity.setImgUrl(adminItemDto.getImgUrl());
        adminItemEntity.setTouristSpotName(adminItemDto.getTouristSpotName());
        adminItemEntity.setAddress(adminItemDto.getAddress());
        adminItemEntity.setContact(adminItemDto.getContact());
        adminItemEntity.setFeatures(adminItemDto.getFeatures());
        adminItemEntity.setBusinessHours(adminItemDto.getBusinessHours());
        adminItemEntity.setContentType(adminItemDto.getContentType());
        // 필요한 다른 필드들도 설정

        return adminItemEntity;
    }
}