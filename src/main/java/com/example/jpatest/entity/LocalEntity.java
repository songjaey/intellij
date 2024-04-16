package com.example.jpatest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class LocalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "local_id")
    private Long id;

    @Column
    private String country;
    @Column
    private String local;

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
