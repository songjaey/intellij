package com.example.jpatest.repository;

import com.example.jpatest.entity.TestMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMemberRepository extends JpaRepository<TestMemberEntity, Long>{

    public TestMemberEntity findByEmailAndPassword(String email, String password);
}
/*
    save() -> insert, update 새로운 데이터를 테이블에 저장, 또는 특정 데이터 변경
    findOne() -> 하나의 데이터를 찾기, primarykey로 지정된 칼럼값을 찾기
    findAll() -> 테이블의 전체 레코드 가져오기 ( 정렬, 페이징 가능)
    count() ->  테이블의 전체 레코드 개수를 알려준다.
    delete() -> 삭제

    findByName("김똥개")
    -> select * from 테이블명 where name='김똥개';

    select * from 테이블명 where email='입력값' and password='입력값'
    findByEmailAndPassword();

    or ->  findByEmailOrPassword();

    칼럼의 두값 사이에 있는 항목 검색
    범위 지정~
    Between -> findByEmailBetween('a','d') // 이메일이 a에서 d 사이의 이메일 찾아오기

    포함된 값 검색
    like -> findByNameLike('만') // 만이라는 글씨가 들어간 Name을 전부 찾는다.

    정렬하여 가져오기
    OrderBy -> findAllOrderByNameDesc() // Desc-내림차순, Asc-오름차순
            -> findByAgeOrderByIdAsc(30) // 나이가 30
 */