package com.example.jpatest.control;

import com.example.jpatest.dto.MemberDto;
import com.example.jpatest.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.naming.Binding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class MainController {

    @Autowired
    MemberService memberService;

    @GetMapping("/")
    public String index() {
        return "main/index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "member/signupForm";
    }

    @GetMapping("/signin")
    public String signinForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        return "member/signinForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberDto memberDto, BindingResult bind, Model model) {
        if (bind.hasErrors()) {
            System.out.println("유효하지 않은 값 ");
        }
        System.out.println("값 ");

        memberService.memberInsert(memberDto);
        return "redirect:/";
    }

    @PostMapping("/signin")
    public String signin(MemberDto memberDto, HttpServletRequest httpServletRequest, Model model){

        //
        memberDto = memberService.memberLogin(memberDto, httpServletRequest.getRemoteAddr());

        httpServletRequest.getSession().invalidate();
        if( memberDto != null) {

            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("user", memberDto);
            session.setMaxInactiveInterval(3600);
        }else{
            model.addAttribute("fail","아이디 또는 비밀번호가 일치하지 않습니다");
            return "member/signinForm";
        }
        return "redirect:/";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().invalidate();
        return "redirect:/";
    }
    @GetMapping("/myinfo")
    public String myinfo(HttpServletRequest httpServletRequest, Model model){
        MemberDto memberDto = (MemberDto) httpServletRequest.getSession().getAttribute("user");
        // 현재 로그인
        model.addAttribute("myInfoDto",memberService.myInfo(memberDto));

        return "member/info";
    }
}
    //
