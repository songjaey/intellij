package com.example.jpatest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${itemImgLocation}") // application.properties의 itemImgLocation 값 주입
    private String uploadDirectory;

    @Value("${eventImgLocation}")
    private String evnetUploadDirectory;

    public String saveImage(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = Paths.get(uploadDirectory + fileName);

            // 파일 저장하기
            Files.copy(imageFile.getInputStream(), filePath);

            return "/images/" + fileName; // 저장된 이미지의 URL 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public String saveEventImage(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            Path filePath = Paths.get(evnetUploadDirectory + fileName);

            // 파일 저장하기
            Files.copy(imageFile.getInputStream(), filePath);

            return "/images/" + fileName; // 저장된 이미지의 URL 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }


}
