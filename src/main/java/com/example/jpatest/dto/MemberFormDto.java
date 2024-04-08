package com.example.jpatest.dto;

import com.example.jpatest.entity.Member;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@Getter
@Setter
public class MemberFormDto {
=======
public class MemberFormDto {
//    @NotBlank(message = "이름은 필수 입력 값이다")
//    private String name;

>>>>>>> d00d910 (prac_intelliJ)
    @NotEmpty(message = "이메일은 필수 입력 값이다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값이다.")
<<<<<<< HEAD
    @Length(min=6, max=16, message = "비밀번호는 6~16자로 입력")
=======
    @Length(min=8, max=16, message = "비밀번호는 8~16자로 입력")
>>>>>>> d00d910 (prac_intelliJ)
    private String password;

    @NotEmpty(message="연락처를 입력하세요")
    private String tel;

    private String zipCode;
<<<<<<< HEAD

=======
>>>>>>> d00d910 (prac_intelliJ)
    @NotEmpty(message="주소는 필수 입력 값이다.")
    private String addr1;

    private String addr2;

<<<<<<< HEAD
    @AssertTrue(message = "(이용약관)만 14세 이상이어야 합니다.")
    private boolean accept1;

    @AssertTrue(message = "이용약관에 동의해야 합니다.")
    private boolean accept2;

    @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
    private boolean accept3;

    public static MemberFormDto createDto(Member member){
        MemberFormDto memberFormDto = new MemberFormDto();
=======
    public static MemberFormDto createDto(Member member){
        MemberFormDto memberFormDto = new MemberFormDto();
        //memberFormDto.setName(member.getName());
>>>>>>> d00d910 (prac_intelliJ)
        memberFormDto.setEmail(member.getEmail());
        memberFormDto.setZipCode(member.getZipCode());
        memberFormDto.setAddr1(member.getAddr1());
        memberFormDto.setAddr2(member.getAddr2());
        return memberFormDto;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> d00d910 (prac_intelliJ)
