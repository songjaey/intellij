package com.example.jpatest.repository;

import com.example.jpatest.entity.LoginHistoryEntity;
import com.example.jpatest.entity.TestMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {

    public List<LoginHistoryEntity> findByTestMemberEntityId(Long test_member_entity_id);
}
