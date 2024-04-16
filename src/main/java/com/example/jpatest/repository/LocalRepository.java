package com.example.jpatest.repository;

import com.example.jpatest.entity.LocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<LocalEntity, Long> {
    Optional<LocalEntity> findByCountryAndLocal(String country, String local);
    boolean existsByCountryAndLocal(String country, String local);
}