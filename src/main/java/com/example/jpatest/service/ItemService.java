package com.example.jpatest.service;

import com.example.jpatest.dto.ItemDto;
import com.example.jpatest.entity.Item;
import com.example.jpatest.entity.ItemImg;
import com.example.jpatest.repository.ItemImgRepo;
import com.example.jpatest.repository.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Service
public class ItemService {

    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private ItemImgRepo itemImgRepo;

    public Item detail(Long id){
        return itemRepo.findById(id).get();
    }


    public Page<Item> allList(Pageable pageable){

        return itemRepo.findAllByOrderByIdDesc(pageable);

    }

    public void imgUpload(MultipartFile image, ItemDto itemDto){
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int min = now.getMinute();
        int sec = now.getSecond();

        String absPath = new File("/User/dw-021/Desktop").getAbsolutePath()+"/";
        String newName = "img"+hour+min+sec;
        String ext ="."+image.getOriginalFilename().replaceAll("^.*\\(.*)$" , "$1");
        String path = "img/"+year+"/"+month+"/"+day;
        try{
            File file = new File(absPath+path);
            if(!file.exists()){
                file.mkdirs();
            }
            file = new File(absPath+path+"/"+newName+ext);
            image.transferTo(file);

            //데이터베이스 저장
            Item item = Item.of(itemDto);
            itemRepo.save(item);

            ItemImg itemImg = new ItemImg();
            itemImg.setImgPath(path);
            itemImg.setImgName(newName+ext);
            itemImg.setOriginalName(image.getOriginalFilename());
            itemImg.setItem(item);

            itemImgRepo.save(itemImg);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
