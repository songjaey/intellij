package com.example.jpatest.service;

import com.example.jpatest.dto.BoardDto;
import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.entity.Board;
import com.example.jpatest.entity.Comment;
import com.example.jpatest.entity.Member;
import com.example.jpatest.repository.BoardRepository;
import com.example.jpatest.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;
    @Autowired
    private final CommentRepository commentRepository;

    public List<Board> BoardList(){
        return boardRepository.findAll();
    }


    public void saveBoard(BoardDto boardDto, Long memeberId){
//        System.out.println("Saving boardDto: " + boardDto); // 데이터 확인
        Board board = Board.createBoard(boardDto, memeberId);
        boardRepository.save(board);
    }
    public void updateBoard(Long id, Board updatedBoard) {
        // ID를 사용하여 데이터베이스에서 해당 Board를 찾습니다.
        Optional<Board> optionalBoard = boardRepository.findById(id);

        // Board를 찾았는지 확인하고 수정된 내용을 적용하여 업데이트합니다.
        optionalBoard.ifPresent(board -> {
            // 수정된 내용을 적용합니다.
            board.setSubject(updatedBoard.getSubject());
            board.setName(updatedBoard.getName());
            board.setContents(updatedBoard.getContents());
            board.setRegdate(LocalDateTime.now());
            // 필요한 경우 다른 속성들도 업데이트할 수 있습니다.

            // 수정된 Board를 데이터베이스에 저장하여 업데이트합니다.
            boardRepository.save(board);
        });
    }

    public void deleteBoardById(Long id){
        boardRepository.deleteById(id);
    }

    public void addComment(Long boardId, Comment comment){
        Optional<Board> boardOptional = boardRepository.findById(boardId);
        if (boardOptional.isPresent()) {
            Board board = boardOptional.get();
            comment.setBoard(board);
            board.getComments().add(comment);
            boardRepository.save(board);
        } else {
            // 게시글을 찾을 수 없을 경우에 대한 처리
            // 예를 들어 예외를 던지거나 다른 처리를 수행할 수 있습니다.
        }
    }

    public boolean deleteComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            commentRepository.delete(commentOptional.get());
            return true; // 댓글 삭제 성공
        } else {
            return false; // 댓글이 존재하지 않아 삭제 실패
        }
    }

    public Page<Board> getBoardsPage(int page, int pageSize) {
        // 페이지 번호는 0부터 시작하므로 -1 처리
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return boardRepository.findAll(pageable);
    }

    public Optional<Board> findBoardById(Long id) {
        return boardRepository.findById(id);
    }

//    public void viewCntUpdate(Long id) {
//        Optional<Board> boardOptional = boardRepository.findById(id);
//        if (boardOptional.isPresent()) {
//            Board board = boardOptional.get();
//            board.setViewcnt(board.getViewcnt() + 1);
//            boardRepository.save(board);
//        }
//     }
}