package com.example.jpatest.control;

import com.example.jpatest.dto.BoardDto;
import com.example.jpatest.dto.CommentDto;
import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.entity.Board;
import com.example.jpatest.entity.Comment;
import com.example.jpatest.entity.Member;
import com.example.jpatest.service.BoardService;
import com.example.jpatest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String main() {
        return "main";
    }

    @GetMapping("/members/login")
    public String login(Model model) {
        return "member/loginForm";
    }

    @PostMapping("/members/login")
    public String processLoginForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류가 있는 경우 처리
            return "member/loginForm";
        }

        // 이메일이 존재하는지 확인
        if (!memberService.emailExists(memberFormDto.getEmail())) {
            bindingResult.rejectValue("email", "email.notFound", "이메일이 존재하지 않습니다.");
            model.addAttribute("loginErrorMsg", "이메일이 존재하지 않습니다.");
            return "member/loginForm";
        }

        // 이메일과 비밀번호가 일치하는지 확인
        if (!memberService.authenticate(memberFormDto.getEmail(), memberFormDto.getPassword())) {
            bindingResult.rejectValue("password", "password.incorrect", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("loginErrorMsg", "비밀번호가 일치하지 않습니다.");
            return "member/loginForm";
        }

        // 로그인 성공
        return "main";
    }

    @GetMapping("/members/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/loginForm"; // 직접적인 뷰 이름을 반환합니다.
    }

    @GetMapping("/members/logout")
    public String logout(){
        return "redirect:/";
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
    public String board(Model model) {
        List<Board> boards = boardService.BoardList();
        model.addAttribute("boards", boards);
        return "notice/help";
    }

    @GetMapping("/board/add")
    public String getAddBoardForm(Model model) {
        model.addAttribute("boardDto", new BoardDto());
        return "notice/helpAdd";
    }

    @PostMapping("/board/helpAdd")
    public String AddBoard(BoardDto boardDto,Model model){
        boardService.saveBoard(boardDto);
        List<Board> boards = boardService.BoardList();
        model.addAttribute("boards", boards);
        return "redirect:/board/help";
    }




    @GetMapping("/board/help/read/{id}")
    public String getAddBoardById(@PathVariable("id") Long id, Model model){
        boardService.viewCntUpdate(id);
        Optional<Board> result = boardService.findBoardById(id);
        Board board = result.get();
        List<Comment> comments = board.getComments(); // 댓글 목록 가져오기
        model.addAttribute("commentDto", new CommentDto()); // commentDto 추가
        model.addAttribute("board", board);
        model.addAttribute("comments", comments); // 댓글 목록 추가
        return "notice/helpRead";
    }

    @GetMapping("/board/help/modif/{id}")
    public String modifyForm(@PathVariable("id") Long id, Model model){
        Optional<Board> result = boardService.findBoardById(id);
        Board board = result.get();
        model.addAttribute("board", board);
        return "notice/helpModif";
    }

    @PostMapping("/board/help")
    public String modifyBoard(@ModelAttribute("board") Board board, @RequestParam("id") Long id) {

        System.out.println(id);
        boardService.updateBoard(id, board); // 수정된 board 엔티티를 저장
        return "redirect:/board/help"; // 수정 후 목록 페이지로 리다이렉트
    }

    @PostMapping("/board/help/delete/{id}")
    public String deleteBoard(@PathVariable("id") Long id, Model model){
        boardService.deleteBoardById(id);
        return "redirect:/board/help";
    }

    @PostMapping("/board/help/comment")
    public String addCommentAndShowBoard(@ModelAttribute("commentDto") CommentDto commentDto) {
        Optional<Board> result = boardService.findBoardById(commentDto.getBoardId());
        Board board = result.get();
        Comment comment = new Comment(commentDto.getAuthor(), commentDto.getContent(), board);
        boardService.addComment(commentDto.getBoardId(), comment);
        return "redirect:/board/help/read/" + commentDto.getBoardId();
    }

    @DeleteMapping("/board/help/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        boardService.deleteComment(id);
        return ResponseEntity.noContent().build();
        //return "redirect:/"; // 적절한 리다이렉트 경로로 수정해야 합니다.
    }



}
