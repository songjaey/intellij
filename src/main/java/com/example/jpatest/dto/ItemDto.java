package com.example.jpatest.dto;


import lombok.Data;


@Data
public class ItemDto {
    private Long id;
    private String itemName;
    private String brand;
    private String cpu;
    private String inch;
    private int ram;
    private String os;
}
