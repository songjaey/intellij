package com.example.jpatest.repository;

import com.example.jpatest.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    public List<Board> findAll();

    Optional<Board> findById(Long id);

    Page<Board> findAll(Pageable pageable);

}
//public Optional<Board> selectOne(String id){
//    List<Board> result = jdbcTemplate.query("select * from test_board where wr_id=?");
//    return result.stream().findAny();
//}
//public void updateViewCnt(String id){
//    jdbcTemplate.update("update test_board set wr_viewcnt = wr_viewcnt + 1 where wr_id = ?",id);
//}