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
public class loginHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long testMemberEntityId;

    @Column
    private String ipAddr;

    @Column
    private LocalDateTime loginDate;
}
