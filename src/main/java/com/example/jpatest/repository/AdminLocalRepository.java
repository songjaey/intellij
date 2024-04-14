package com.example.jpatest.repository;

import com.example.jpatest.entity.AdminLocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminLocalRepository extends JpaRepository<AdminLocalEntity, Long> {
}
