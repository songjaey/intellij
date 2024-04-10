package com.example.jpatest.service;

import com.example.jpatest.dto.BoardDto;
import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.entity.Board;
import com.example.jpatest.entity.Member;
import com.example.jpatest.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<Board> BoardList(){
        return boardRepository.findAll();
    }

    public void saveBoard(BoardDto boardDto){
        System.out.println("Saving boardDto: " + boardDto); // 데이터 확인
        Board board = Board.createBoard(boardDto);

    }

    public Optional<Board> findBoardById(String id) {
        return boardRepository.findById(id);
    }
    public void viewCntUpdate(String id){
        boardRepository.updateViewCnt(id);
    }
}