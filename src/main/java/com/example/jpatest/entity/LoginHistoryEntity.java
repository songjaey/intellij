package com.example.jpatest.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="login_history")
public class LoginHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

//    @ManyToOne
//    @JoinColumn(name="test_member_entity_id")
//    private TestMemberEntity testMemberEntity;

    @Column
    private String ipAddr;

    @Column
    private LocalDateTime loginDate;
}

/*

    외래키 : 한 테이블의 컬럼이 다른 테이블의 기본키를 참조하는 경우
    member테이블 id 컬럼의 값이 history테이블의 member_id 컬럼에 저장되는 경우
    create table customer(
        id int primary key,
        name
    )
    create table bank(
        account_num int primary key,
        balance int,
        customer_id int,
        foreign key(customer_id) references customer(id)
    );

    연관 관계 정의 규칙
    1. 방향 설정- 단방향, 양방향
    2. 연관 관계의 주인 : 단방향일 때는 외래키를 가지는 테이블이 주인이다.
                        양방향일 때는 mappedBy 로 설정을 해줘야한다.
                     연관 관계의 주인은 조회, 수정, 저장, 삭제를 할 수 있다.주인이 아니면 조회만 가능하다.
    3. 다중성 : 일대일, 다대일, 일대다, 다대다

       다대일 - 게시글에 여러개의 댓글 작성 가능
                하나의 댓글은 하나의 게시글에 작성한다.
                댓글과 게시글은 다대일 관계를 갖는다.

                외래키는 댓글에 존재한다.
                1. 댓글 entity에 게시글 참조변수를 둔다.
                2. 게시글 참조변수에 @ManyToOne 설정
                3. @JoinColumn(name="게시글 기본키 컬럼") 설정

                양방향
                단방향 세팅에 추가로 작업
                4. 게시글 entity에 댓글 다수 저장공간 생성
                5. @OneToMany(mappedBy="게시글테이블명")

      일대일
        외래키를 주 테이블에 포함한다.
            회원 테이블과 회원사진이 등록되는 이미지테이블이 있다면
            회원테이불애 이미지 테이블의 기본키를 외래키로 둔다.

            양방향
                이미지 테이블에 mappedBy를 설정한다.

        일대 다
            주 테이블이 일이 디ㅗ고 대상 테이블이 다가 되는 관계
            회원테이블이 주 테이블이고, 이미지 테이블이 대상 테이블이라면
            회원 테이블에 이미지테이블의 기본키를 외래키로 설정해야한다.
            데이터베이스에서는 주테이블을 다 쪽으로 본다.
            그래서 회원객체로 이미지 객체를 저장하게 되면 insert 한번 동작하고, update가 또 한번 동작한다.

         다대다
            쇼밍몰에서 상품을 장바구니 담아두는 기능을 구현
            장바구니 테이블과 상품 테이블 다 대 일 관계를 가지지만
            상품의 수량이 여러개인 상태에서 다대다로 관계를  가지게 되면
            조인 쿼리문이 복잡해진다. 숨겨져있는 뷰 테이블이 존재하게 될 수도 있다.
            다대다를 해야되는 경우에는 일대다, 다대일로 풀어서 만드는 것이 좋다.

         Optional은 자바의 내장 클래스이다.
         데이터를 다루는 객체를 생성하거나 할 때 사용되는 클래스
         데이터를 읽어서 객체로 생성할 때 데이터가 없다면 객체는 생성되지 않는다.
         이때 optional을 이용하면 코드의 안정성을 확보할 수 있다.
         데이터를 읽어서 optional로  저장하고 optional에 null이 있는지 여부를 확인하여 동작시킨다.


     */