package com.example.jpatest.entity;

import com.example.jpatest.constant.Role;
import com.example.jpatest.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="login_member")
public class Member extends BaseEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Column
//    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String zipCode;

    @Column
    private String addr1;

    @Column
    private String addr2;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        //member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());

        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);

        member.setZipCode(memberFormDto.getZipCode());
        member.setAddr1(memberFormDto.getAddr1());
        member.setAddr2(memberFormDto.getAddr2());
        member.setRole(Role.USER);
        return member;
    }

//    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
//        Member member = new Member();
//        member.setName( memberFormDto.getName());
//    }
}

