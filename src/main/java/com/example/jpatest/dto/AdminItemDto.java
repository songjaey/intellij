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
    private String imgUrl; // 이미지 URL을 저장할 필드
    private Map<String, String> businessHours; // 영업시간을 저장할 필드
    private String contentType;

    // 이미지 URL을 설정하는 setter 메서드


    public void setBusinessHours(String key, String value) {
        if (this.businessHours == null) {
            this.businessHours = new HashMap<>();
        }
        this.businessHours.put(key, value);
    }

}
