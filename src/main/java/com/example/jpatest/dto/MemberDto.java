package com.example.jpatest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private long id;

    @NotEmpty(message = "이메일을 입력하세요")
    private String email;

    @NotEmpty(message = "이름을 입력하세요")
    private String name;

    @NotEmpty(message = "비번을 입력하세요")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z]).{4,16}", message="영어소문자, 숫자 포함 4-16자리로 입력하세요")
    private String password;

    @NotEmpty(message = "연락처를 입력하세요")
    private String tel;

//    public static MemberDto toDto(TestMemberEntity testMemberEntity){
//        return MemberDto.builder().id(testMemberEntity.getId()).email(testMemberEntity.getEmail())
//                .name(testMemberEntity.getName()).password(testMemberEntity.getPassword()).tel(testMemberEntity.getTel()).build();
//
//    }

}
