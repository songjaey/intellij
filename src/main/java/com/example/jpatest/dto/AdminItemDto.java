package com.example.jpatest.dto;

import com.example.jpatest.entity.AdminItemEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AdminItemDto {
    private String touristSpotName;
    private String address;
    private String contact;
    private String features;
    private String businessHours; // 영업시간
    private String imgUrl;

    private static ModelMapper mapper = new ModelMapper();
    public static AdminItemDto of(AdminItemEntity adminItemEntity) {
        ModelMapper mapper = new ModelMapper(); // ModelMapper 인스턴스 생성
        return mapper.map(adminItemEntity, AdminItemDto.class);
    }
}
