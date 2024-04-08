package com.example.jpatest.dto;

import com.example.jpatest.entity.Member;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberFormDto {
    @NotEmpty(message = "이메일은 필수 입력 값이다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값이다.")
    @Length(min=6, max=16, message = "비밀번호는 6~16자로 입력")
    private String password;

    @NotEmpty(message="연락처를 입력하세요")
    private String tel;

    private String zipCode;

    @NotEmpty(message="주소는 필수 입력 값이다.")
    private String addr1;

    private String addr2;

    @AssertTrue(message = "(이용약관)만 14세 이상이어야 합니다.")
    private boolean accept1;

    @AssertTrue(message = "이용약관에 동의해야 합니다.")
    private boolean accept2;

    @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
    private boolean accept3;

    public static MemberFormDto createDto(Member member){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(member.getEmail());
        memberFormDto.setZipCode(member.getZipCode());
        memberFormDto.setAddr1(member.getAddr1());
        memberFormDto.setAddr2(member.getAddr2());
        return memberFormDto;
    }
}