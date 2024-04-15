package com.example.jpatest.repository;

import com.example.jpatest.entity.LocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<LocalEntity, Long> {
    // 추가적인 메서드가 필요한 경우 여기에 선언
}