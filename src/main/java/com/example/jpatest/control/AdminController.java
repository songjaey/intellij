package com.example.jpatest.control;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.entity.AdminEventEntity;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.service.AdminEventService;
import com.example.jpatest.service.AdminItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminEventService adminEventService;
    private final AdminItemService adminItemService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);


    @Autowired
    public AdminController(AdminEventService adminEventService, AdminItemService adminItemService) {
        this.adminEventService = adminEventService;
        this.adminItemService = adminItemService;
    }

    private LocalRepository localRepository;

    @GetMapping("/first")
    public String itemForm(Model model) {  //상품 작성 페이지 제공
        return "adminhub/first";
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
            // DB에 국가와 지역 정보 저장
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

    @PostMapping(value = "/item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createAdminItem(@ModelAttribute @Valid AdminItemDto adminItemDto,
                                             @RequestParam("imageFile") MultipartFile imageFile,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 오류가 있는 경우
            return ResponseEntity.badRequest().body("입력 데이터가 유효하지 않습니다.");
        }

        try {
            // 이미지를 저장하고 저장된 경로를 DTO에 설정
            String imageUrl = saveImage(imageFile);
            adminItemDto.setImgUrl(imageUrl);

            // 관리 항목 생성 및 저장
            AdminItemEntity adminItemEntity = adminItemService.createAdminItem(adminItemDto);

            return new ResponseEntity<>(adminItemEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create admin item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("관광지 생성에 실패했습니다.");
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