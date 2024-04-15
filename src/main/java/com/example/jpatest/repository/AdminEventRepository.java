package com.example.jpatest.repository;

import com.example.jpatest.entity.AdminEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEventRepository extends JpaRepository<AdminEventEntity, Long> {

}
