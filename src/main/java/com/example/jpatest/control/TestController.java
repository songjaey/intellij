package com.example.jpatest.control;

import com.example.jpatest.test.TestMember;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("msg","ㅎㅎㅎ");
        List<String> names = new ArrayList<>();
        names.add("강감찬"); names.add("문익점"); names.add("최무선");
        names.add("이순신"); names.add("김춘추");
        int age=25;

        model.addAttribute("name", names);
        model.addAttribute("age", age);

        TestMember testMember = new TestMember();
        testMember.setAge(29);
        testMember.setName("김문수");
        testMember.setTel("01023456789");
        testMember.setEmail("pms8@naver.com");
        model.addAttribute("testMember", testMember);

        return "test.html";
    }

//    @GetMapping("/layout")
//    public String layout(){
//
//        return "layout.html";
//    }

    @GetMapping("/layout")
    public String layout(){
        return "body";
    }

    @GetMapping("/join")
    public String join(){
        return "join";
    }

}

