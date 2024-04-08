package com.example.jpatest.control;

import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.service.ItemService;
import com.example.jpatest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/")
    public String main(){

        return "default";
    }

    @GetMapping("/members/login")
    public String login(Model model){
        return "/member/loginForm";
    }
    @GetMapping("/members/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/loginForm";
    }

    @GetMapping("members/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("members/new")
    public String newMember(@Valid MemberFormDto memberFormDto,
                            BindingResult bindingResult, Model model){
        if( bindingResult.hasErrors()  ){
            return "member/memberForm";
        }

        try{
            memberService.saveMember(memberFormDto, passwordEncoder );
        }catch(IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }


}
    //
