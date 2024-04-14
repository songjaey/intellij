package com.example.jpatest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "admin_local")
public class AdminLocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String touristSpotName;
    private String address;
    private String phoneNumber;
    @ElementCollection
    @CollectionTable(name = "admin_local_opening_hours", joinColumns = @JoinColumn(name = "admin_local_id"))
    @MapKeyColumn(name = "day_of_week")
    @Column(name = "hours")
    private Map<String, String[]> openingHours;


    // 생성자, 게터, 세터 등 필요한 코드 추가

    public AdminLocalEntity() {
    }
}
