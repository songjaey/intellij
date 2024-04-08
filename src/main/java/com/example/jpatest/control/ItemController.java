package com.example.jpatest.control;

import com.example.jpatest.dto.ItemDto;
import com.example.jpatest.entity.Item;
import com.example.jpatest.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

@Controller
public class ItemController {

    @Autowired
    ItemService itemService;

    @GetMapping("/notebook")
    public String main(@PageableDefault(size=10) Pageable pageable, Model model){

        Page<Item> list = itemService.allList(pageable);
        model.addAttribute("itemDtoList", list.getContent());
        model.addAttribute("totalPage",list.getTotalPages());
        model.addAttribute("page", list.getNumber());
        return "item/index";
    }
    @GetMapping("/notebook/write")
    public String writeForm(Model model){
        model.addAttribute("itemDto", new ItemDto());
        return "item/writeForm";
    }

    @PostMapping("/notebook/write")
    public String write(ItemDto itemDto, @RequestParam("img") MultipartFile image, Model model){

        //Long id = itemService.insertWrite(itemDto);
        itemService.imgUpload(image, itemDto);
        return "redirect:/notebook";
    }

    @GetMapping("/notebook/detail/{id}")
    public String detail(@PathVariable Long id, Model model){

        model.addAttribute("item", itemService.detail(id));
        return "item/detail";
    }

    @GetMapping("/notebook/update/{id}")
    public String update(@PathVariable Long id, Model model){
        model.addAttribute("itemDto",itemService.getItemDtl(id));
        return "item/writeForm";
    }

    @PostMapping("/notebook/update/{id}")
    public String update(ItemDto itemDto, Model model){

        itemService.updateItem(itemDto);

        return "redirect:/notebook/detail/"+itemDto.getId();
    }

    @GetMapping("/notebook/delete/{id}")
    public String delete(@PathVariable Long id, Model model){

        itemService.deleteItem(id);

        return "redirect:/notebook";
    }
}
