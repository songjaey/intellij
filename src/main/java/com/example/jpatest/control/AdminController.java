package com.example.jpatest.control;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.entity.AdminEventEntity;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.service.AdminEventService;
import com.example.jpatest.service.AdminItemService;
import com.example.jpatest.service.FileUploadService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminEventService adminEventService;
    private final AdminItemService adminItemService;
    private final FileUploadService fileUploadService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final LocalRepository localRepository;

    @Autowired
    public AdminController(AdminEventService adminEventService, AdminItemService adminItemService, LocalRepository localRepository, FileUploadService fileUploadService) {
        this.adminEventService = adminEventService;
        this.localRepository = localRepository;
        this.adminItemService = adminItemService;
        this.fileUploadService = fileUploadService;
    }



    @GetMapping("/first")
    public String itemForm(Model model) {
        return "adminhub/first"; // 상품 작성 페이지 제공
    }

    @GetMapping("/getAllLocals")
    @ResponseBody
    public ResponseEntity<List<LocalEntity>> getAllLocals() {
        try {
            List<LocalEntity> locals = localRepository.findAll();
            return ResponseEntity.ok(locals);
        } catch (Exception e) {
            logger.error("모든 로컬 정보를 가져오는 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/saveLocal")
    @ResponseBody
    public ResponseEntity<String> saveLocal(@RequestParam("country") String country,
                                            @RequestParam("local") String local) {
        try {
            LocalEntity newLocal = new LocalEntity(country, local);
            localRepository.save(newLocal);
            return ResponseEntity.ok("저장되었습니다!");
        } catch (Exception e) {
            logger.error("국가와 지역 정보 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
        }
    }

    @GetMapping("/localDetail")
    public String showLocalDetail(@RequestParam("country") String country,
                                  @RequestParam("local") String local,
                                  Model model) {
        // 로직 구현
        return "adminhub/localDetail";
    }

    @GetMapping("/eventCustom")
    public String getEvents(Model model) {
        List<AdminEventEntity> events = adminEventService.getAllEvents();
        model.addAttribute("events", events);
        return "adminhub/eventCustom";
    }

    @PostMapping("/saveEvent")
    public ResponseEntity<String> saveEvent(@RequestParam("image") MultipartFile image,
                                            @RequestParam("content") String content) {
        try {
            // 이미지를 저장하고 저장된 경로를 얻어옴
            String imgUrl = saveEventImage(image);

            // AdminEventEntity 객체 생성 및 저장
            AdminEventEntity event = new AdminEventEntity();
            event.setContent(content);
            event.setImgUrl(imgUrl);
            adminEventService.saveEvent(event);

            return ResponseEntity.ok("이벤트가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            logger.error("이벤트 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이벤트를 저장하는 데 실패했습니다.");
        }
    }

    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        try {
            adminEventService.deleteEvent(id);
            return ResponseEntity.ok("이벤트가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이벤트를 삭제하는 데 실패했습니다.");
        }
    }

    // 이미지를 저장하는 메서드

    @PostMapping("/item")
    @ResponseBody
    public ResponseEntity<String> createAdminItem(@RequestParam("imageFile") MultipartFile imageFile,
                                                  @RequestParam("touristSpotName") String touristSpotName,
                                                  @RequestParam("address") String address,
                                                  @RequestParam("contact") String contact,
                                                  @RequestParam("features") String features,
                                                  @RequestParam("businessHours") String businessHoursJson) {

        if (imageFile.isEmpty() || touristSpotName.isEmpty() || address.isEmpty() || contact.isEmpty() || features.isEmpty() || businessHoursJson.isEmpty()) {
            return ResponseEntity.badRequest().body("모든 필수 항목을 입력해주세요.");
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> businessHours = objectMapper.readValue(businessHoursJson, new TypeReference<Map<String, String>>() {});

            String imageUrl = fileUploadService.saveImage(imageFile); // 이미지 저장 및 URL 반환

            AdminItemDto adminItemDto = new AdminItemDto();
            adminItemDto.setImageUrl(imageUrl);
            adminItemDto.setTouristSpotName(touristSpotName);
            adminItemDto.setAddress(address);
            adminItemDto.setContact(contact);
            adminItemDto.setFeatures(features);
            adminItemDto.setBusinessHours(businessHours);

            adminItemService.saveAdminItem(adminItemDto);

            return ResponseEntity.ok().body(imageUrl); // 이미지 URL 반환
        } catch (Exception e) {
            logger.error("상품 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
        }
    }



    private String saveImage(MultipartFile imageFile) {
        String uploadDirectory = "C:/TravelGenius/item/";

        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = Paths.get(uploadDirectory + fileName);

        try {
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @GetMapping("/images/{imageName}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) {
        Path imagePath = Paths.get("C:/TravelGenius/item/", imageName); // 이미지가 저장된 경로
        Resource resource;
        try {
            resource = new UrlResource(imagePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }



    private String saveEventImage(MultipartFile imageFile) {
        String uploadDirectory = "C:/TravelGenius/event/";
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = Paths.get(uploadDirectory + fileName);

        try {
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

}
