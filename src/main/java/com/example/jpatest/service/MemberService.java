package com.example.jpatest.service;

import com.example.jpatest.dto.MemberDto;
import com.example.jpatest.entity.LoginHistoryEntity;
import com.example.jpatest.entity.TestMemberEntity;
import com.example.jpatest.repository.LoginHistoryRepository;
import com.example.jpatest.repository.TestMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    TestMemberRepository testMemberRepository;
    @Autowired
    LoginHistoryRepository loginHistoryRepository;
    public void memberInsert(MemberDto memberDto){

        TestMemberEntity testMemberEntity = TestMemberEntity.toEntity(memberDto);
        testMemberRepository.save(testMemberEntity);
    }

    public MemberDto memberLogin(MemberDto memberDto, String ip) {
        TestMemberEntity testMemberEntity = testMemberRepository.findByEmailAndPassword(memberDto.getEmail(), memberDto.getPassword());
        if (testMemberEntity != null) {
            //로그인 성공시 로그인 기록 저장
            LoginHistoryEntity loginHistoryEntity = new LoginHistoryEntity();
            loginHistoryEntity.setIpAddr(ip);
            loginHistoryEntity.setLoginDate(LocalDateTime.now());
            loginHistoryEntity.setTestMemberEntity( testMemberEntity );
            loginHistoryRepository.save(loginHistoryEntity);
            return MemberDto.toDto(testMemberEntity);
        }
        return null;
    }

    public TestMemberEntity myInfo(MemberDto memberDto){
//        TestMemberEntity testMemberEntity = TestMemberEntity.toEntity(memberDto);
//        testMemberEntity.setLoginHistoryEntityList(
//                loginHistoryRepository.findByTestMemberEntityId(testMemberEntity.getId()) );

        Optional<TestMemberEntity> optional = testMemberRepository.findById(memberDto.getId());
        TestMemberEntity testMemberEntity = optional.get();
        // optional.isEmpty();

        return testMemberEntity;
    }

}
