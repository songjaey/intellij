package com.example.jpatest.dto;

import com.example.jpatest.entity.ItemImg;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ItemImgDto {
    private Long id;
    private String originalName;
    private String imgName;
    private String imgPath;

    private static ModelMapper mapper = new ModelMapper();

    public ItemImg of(){
        return mapper.map(this,ItemImg.class);
    }
}
