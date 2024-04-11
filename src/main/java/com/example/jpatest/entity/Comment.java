package com.example.jpatest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_board")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // Comment와 Board 사이의 ManyToOne 관계

    public Comment(String author, String content, Board board) {
        this.author = author;
        this.content = content;
        this.board = board;
    }


    // 다른 필드들과 메소드들...
}
