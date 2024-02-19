package com.example.jpatest.dto;

import com.example.jpatest.entity.LoginHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryDto {


        private long id;

        private long testMemberEntityId;

        private String ipAddr;

        private LocalDateTime loginDate;

//        public static LoginHistoryDto toDto(LoginHistoryEntity loginHistoryEntity){
//            return LoginHistoryDto.builder().id(loginHistoryEntity.getId()).testMemberEntityId(loginHistoryEntity.getTestMemberEntityId())
//                    .ipAddr(loginHistoryEntity.getIpAddr()).loginDate(loginHistoryEntity.getLoginDate()).build();
//
//        }

}


