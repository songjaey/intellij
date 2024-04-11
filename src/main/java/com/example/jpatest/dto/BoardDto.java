package com.example.jpatest.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private String email;
    private String subject;
    private String contents;
    private String name;
    private String viewcnt;

    public BoardDto(MemberFormDto memberFormDto) {
        this.email = memberFormDto.getEmail();
    }

}