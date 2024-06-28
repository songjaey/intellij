package com.example.jpatest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String email;
    private Long boardId;
    private String author;
    private String content;

    public CommentDto(Long boardId, MemberFormDto memberFormDto) {
        this.email = memberFormDto.getEmail();
        this.boardId = boardId;
    }
}