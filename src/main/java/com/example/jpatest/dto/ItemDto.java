package com.example.jpatest.dto;


import com.example.jpatest.entity.Item;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;


@Data
public class ItemDto {
    private Long id;
    private String itemName;
    private String brand;
    private String cpu;
    private String inch;
    private int ram;
    private String os;
    private int cost;
    private LocalDateTime regDate;
    private String seller;

    private static ModelMapper mapper = new ModelMapper();

    public static ItemDto of(Item item){
        return mapper.map(item, ItemDto.class);
    }
}
