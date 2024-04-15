package com.example.jpatest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "admin_item")
public class AdminItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tourist_spot_name")
    private String touristSpotName;

    @Column(name = "address")
    private String address;

    @Column(name = "contact")
    private String contact;

    @Column(name = "features")
    private String features;

    @Column(name = "business_hours", columnDefinition = "jsonb")
    @Convert(converter = com.example.jpatest.Converter.MapToJsonConverter.class)
    private Map<String, String> businessHours;

    @Column(name = "img_url")
    private String imgUrl; // 이미지 경로

    // 이미지 URL을 가져오는 메서드
    public String getImgUrl() {
        // 이미지 경로를 가공하여 완전한 URL을 반환 (예: "/images/12345.jpg")
        return "/images/" + id + ".jpg"; // 이미지의 ID를 활용한 예시
    }
}