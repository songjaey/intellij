package com.example.jpatest.service;

import com.example.jpatest.dto.AdminItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminItemUploadService {

    @Autowired
    private AdminItemService adminItemService;

    public void uploadAdminItem(AdminItemDto adminItemDto) {
        // 이미지 업로드 로직을 수행하여 adminItemDto에 imgUrl 설정

        // 관리 항목 생성 및 저장
        adminItemService.createAdminItem(adminItemDto);
    }
}
