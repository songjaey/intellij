package com.example.jpatest.control;

import com.example.jpatest.dto.BoardDto;
import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.entity.Board;
import com.example.jpatest.service.BoardService;
import com.example.jpatest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final BoardService boardService;

    @GetMapping("/")
    public String main(){

        return "main";
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

    @GetMapping("/members/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping("/members/new")
    public String newMember(@Valid MemberFormDto memberFormDto,
                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // 필드 검증 오류가 있는 경우
            System.out.println("validationError");
            return "member/memberForm"; // 회원가입 폼을 다시 표시
        }

        try {
            memberService.saveMember(memberFormDto, passwordEncoder);
        } catch (IllegalStateException e) {
            // 회원가입 중 예외 발생 시
            model.addAttribute("emailError", "이미 사용 중인 이메일입니다.");
            System.out.println("ExceptionError");
            return "member/memberForm"; // 회원가입 폼을 다시 표시
        }

        return "redirect:/"; // 회원가입 성공 시 메인 페이지로 리다이렉트
    }

    @GetMapping("/members/findId")
    public String findIdGet(Model model){
        model.addAttribute("tel", ""); // 빈 문자열로 초기화하여 전화번호 필드를 전달
        return "member/findId";
    }
    @PostMapping("/members/findId")
    public String findIdPost(@NotBlank @RequestParam String tel, Model model) {
        try {
            String foundId = memberService.findIdByTel(tel);
            model.addAttribute("foundId", foundId); // 찾은 아이디를 모델에 추가
            return "member/findId"; // 찾은 아이디를 보여주는 뷰로 이동
        } catch (IllegalStateException e) {
            // 해당 전화번호로 가입된 회원이 없는 경우
            model.addAttribute("findIdErrorMsg", e.getMessage());
            model.addAttribute("tel", tel); // 입력한 전화번호를 다시 모델에 추가
            return "member/findId"; // 아이디 찾기 폼을 다시 표시
        }
    }


    @GetMapping("/board/help")
    public String board(Model model){
        List<Board> boards = boardService.BoardList();
        model.addAttribute("boards", boards);
        return "notice/help";
    }

    @GetMapping("/board/add")
    public String getAddBoardForm(Model model) {
        model.addAttribute("board", new Board());
        return "notice/helpAdd"; // 적절한 뷰 이름으로 수정해야 합니다.
    }

    @GetMapping("/board/add/{id}")
    public String addBoard(@PathVariable("id") String id, Model model){
        System.out.println("problem1");
        boardService.viewCntUpdate(id);
        System.out.println("problem2");
        Optional<Board> result = boardService.findBoardById(id);
        System.out.println("problem3");
        Board board = result.get();
        System.out.println("problem4");
        model.addAttribute("board", new BoardDto());
        return "notice/helpAdd";
    }

//    @PostMapping("/notice/{id}")
//    public String addForm(Model model){
//
//
//        model.addAttribute("board", new BoardDto());
//
//
//        return "notice/helpAdd";
//    }

}
//
