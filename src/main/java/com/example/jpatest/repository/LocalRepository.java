package com.example.jpatest.repository;

import com.example.jpatest.entity.LocalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<LocalEntity, Long> {
    Optional<LocalEntity> findByCountryAndLocal(String country, String local);
    boolean existsByCountryAndLocal(String country, String local);

    void deleteByCountryAndLocal(String country, String local);

    @Query("SELECT l FROM LocalEntity l WHERE l.id IN :ids")
    List<LocalEntity> findByIds(@Param("ids") List<Long> ids);

}