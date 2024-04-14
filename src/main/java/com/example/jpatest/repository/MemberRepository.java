package com.example.jpatest.repository;

import com.example.jpatest.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Member findByTel(String tel);

    boolean existsByEmail(String email);

}