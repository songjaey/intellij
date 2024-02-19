package com.example.jpatest.control;

import com.example.jpatest.dto.ItemDto;
import com.example.jpatest.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ItemController {

//    @Autowired
//    ItemService itemService;

    @GetMapping("/notebook")
    public String main(Model model){

        return "item/index";
    }
    @GetMapping("/notebook/write")
    public String writeForm(Model model){
        model.addAttribute("itemDto", new ItemDto());
        return "item/writeForm";
    }

    @PostMapping("/notebook/write")
    public String write(ItemDto itemDto, Model model){

        return "redirect:/notebook";
    }
}
