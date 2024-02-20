package com.example.jpatest.entity;

import com.example.jpatest.dto.ItemImgDto;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ItemImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalName;
    @Column(nullable = false)
    private String imgName;
    @Column(nullable = false)
    private String imgPath;

    @OneToOne
    @JoinColumn(name="item_id")
    private Item item;

    private static ModelMapper mapper = new ModelMapper();
    public static ItemImgDto of(ItemImg itemImg){
        return mapper.map(itemImg, ItemImgDto.class);
    }
}
