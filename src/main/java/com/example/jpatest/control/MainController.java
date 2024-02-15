package com.example.jpatest.control;

import com.example.jpatest.dto.MemberDto;
import com.example.jpatest.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.naming.Binding;
import javax.validation.Valid;

@Controller
public class MainController {

    MemberService memberService;
    @GetMapping("/")
    public String index(){
        return "main/index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute("memberDto", new MemberDto());
        return "member/signupForm";
    }

    @GetMapping("/signin")
    public String signinForm(Model model){
        model.addAttribute("memberDto", new MemberDto());
        return "member/signinForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberDto memberDto, BindingResult bind, Model model){
        if( bind.hasErrors()) {
            System.out.println("유효하지 않은 값 ");
        }
        return "redirect:/";
    }

    memberService.memberInsert(memberDto);
}
