package com.example.jpatest.entity;

import com.example.jpatest.dto.MemberDto;
import lombok.*;
import org.aspectj.weaver.ast.Test;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String tel;

//    @OneToMany(mappedBy = "testMemberEntity")
//    List<LoginHistoryEntity> loginHistoryEntityList = new ArrayList<>();

    public static TestMemberEntity toEntity(MemberDto memberDto){
        return TestMemberEntity.builder().email(memberDto.getEmail())
                .name(memberDto.getName()).tel(memberDto.getTel())
                .password(memberDto.getPassword()).id(memberDto.getId()).build();
    }
}
