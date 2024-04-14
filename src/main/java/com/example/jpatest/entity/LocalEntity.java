package com.example.jpatest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class LocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String local;
    // 기본 생성자 추가
    public LocalEntity() {
        // 기본 생성자는 비워두거나 초기화 작업을 추가할 수 있습니다.
    }

    public LocalEntity(String country, String local) {
        this.country = country;
        this.local = local;
    }

    @Override
    public String toString() {
        return "LocalEntity{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", local='" + local + '\'' +
                '}';
    }
}
