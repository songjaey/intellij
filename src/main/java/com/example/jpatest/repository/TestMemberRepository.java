package com.example.jpatest.repository;

import com.example.jpatest.entity.TestMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMemberRepository extends JpaRepository<TestMemberEntity, Long>{

}
