package com.example.jpatest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LocalDto {
    private String country;
    private String local;

    public LocalDto() {
    }
}