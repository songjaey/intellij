package com.example.jpatest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AdminLocalDto {
    private String imageFile; // 이미지 파일 업로드를 위한 필드
    private String touristSpotName; // 관광지명
    private String address; // 주소
    private String phoneNumber; // 연락처
    private Map<String, String[]> openingHours; // 요일별 영업시간 정보

    // 생성자, getter, setter 등을 필요에 따라 구현할 수 있습니다.

    public AdminLocalDto() {
        this.openingHours = new HashMap<>();
    }

    // 요일별 영업시간 설정 메서드
    public void setOpeningHour(String dayOfWeek, String amTime, String pmTime) {
        String[] hours = {amTime, pmTime};
        this.openingHours.put(dayOfWeek, hours);
    }
}
