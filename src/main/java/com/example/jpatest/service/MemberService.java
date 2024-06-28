package com.example.jpatest.service;


import com.example.jpatest.dto.MemberFormDto;
import com.example.jpatest.entity.Member;
import com.example.jpatest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입
    //회원가입폼의 내용을 데이터베이스에 저장(회원가입)
    public void saveMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        //이메일 중복 여부
        validEmail(member);
        memberRepository.save(member);
    }

    public void modifPw(String email, String temporaryPassword, PasswordEncoder passwordEncoder){
        Member member = memberRepository.findByEmail(email);
        member.setPassword(passwordEncoder.encode(temporaryPassword) );
        memberRepository.save(member);
    }

    public void modifyMember(Member member) {
        memberRepository.save(member);
    }

    public void changePw(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        if (member != null) {
            // 비밀번호 해싱
            String encodedPassword = passwordEncoder.encode(password);
            member.setPassword(encodedPassword);
            memberRepository.save(member);
        } else {
            throw new MemberNotFoundException("User not found");
        }
    }
    public class MemberNotFoundException extends RuntimeException {
        public MemberNotFoundException(String message) {
            super(message);
        }
    }

    private void validEmail(Member member){
        Member find = memberRepository.findByEmail(member.getEmail());
        if(find != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
    public Member findByEmail(String email){
        return memberRepository.findByEmail(email);
    }

    public String findIdByTel(String tel) {
        Member member = memberRepository.findByTel(tel);

        if (member != null && member.getEmail() != null) {
            return member.getEmail(); // 해당 회원의 ID 반환
        } else {
            throw new IllegalStateException("해당 전화번호로 가입된 회원이 없습니다.");
        }
    }

    public boolean emailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean authenticate(String email, String password) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            return false;
        }
        return passwordEncoder.matches(password, member.getPassword());
    }
    public Member loadUserByUserId(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return member;
        } else {
            return null;
        }
    }

    public boolean checkEmailAndPhoneNumberMatch(String email, String tel) {

        Member member = memberRepository.findByEmail(email);

        // 조회된 회원이 없으면 false를 반환합니다.
        if (member.getEmail() == null) {
            return false;
        }

        return member.getTel().equals(tel);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        Member member = memberRepository.findByEmail(username);
        if( member == null){
            throw new UsernameNotFoundException(username);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}