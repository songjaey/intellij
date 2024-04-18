package com.example.jpatest.control;

import com.example.jpatest.dto.AdminItemDto;
import com.example.jpatest.dto.LocalDto;
import com.example.jpatest.entity.AdminEventEntity;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.AdminItemRepository;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.service.AdminEventService;
import com.example.jpatest.service.AdminItemService;
import com.example.jpatest.service.AdminLocalService;
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
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminEventService adminEventService;
    private final AdminItemService adminItemService;

    private final AdminItemRepository adminItemRepository;
    private final FileUploadService fileUploadService;
    private final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final LocalRepository localRepository;

    private final AdminLocalService adminLocalService;

    @Autowired
    public AdminController(AdminEventService adminEventService, AdminItemService adminItemService,AdminItemRepository adminItemRepository,
                           LocalRepository localRepository, AdminLocalService adminLocalService, FileUploadService fileUploadService) {
        this.adminEventService = adminEventService;
        this.localRepository = localRepository;
        this.adminItemRepository = adminItemRepository;
        this.adminItemService = adminItemService;
        this.adminLocalService = adminLocalService;
        this.fileUploadService = fileUploadService;
    }



    @GetMapping("/first")
    public String itemForm(Model model) {
        model.addAttribute("localDto",new LocalDto());
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
            // 이미 존재하는지 확인
            if (localRepository.existsByCountryAndLocal(country, local)) {
                return ResponseEntity.badRequest().body("이미 존재하는 지역입니다.");
            }

            LocalEntity newLocal = new LocalEntity(country, local);
            localRepository.save(newLocal);
            return ResponseEntity.ok("저장되었습니다!");
        } catch (Exception e) {
            logger.error("국가와 지역 정보 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
        }
    }

    @RequestMapping(value = "/deleteLocal", method = RequestMethod.DELETE)
    public String deleteLocalDetail(@RequestParam("country") String country,
                                    @RequestParam("local") String local,
                                    Model model) {
        adminLocalService.deleteDetail(country, local);

        return "adminhub/first";
    }
    @GetMapping("/localDetail")
    public String showLocalDetail(@RequestParam("country") String country,
                                  @RequestParam("local") String local,
                                  @RequestParam("content") String contentType,
                                  Model model) {

        // country와 local에 해당하는 LocalEntity 찾기
        Optional<LocalEntity> optionalLocalEntity = localRepository.findByCountryAndLocal(country, local);

        if (optionalLocalEntity.isPresent()) {
            LocalEntity localEntity = optionalLocalEntity.get();


            // URL에 따라 contentType 결정
            String contentType1 = determineContentType(contentType); // determineContentType 메서드 구현 필요

            // localEntity와 contentType에 따라 admin item 가져오기
            List<AdminItemEntity> adminItemEntity = adminItemRepository.findByLocalAndContentType(localEntity, contentType1);

            model.addAttribute("adminItemDto", new AdminItemDto());
            model.addAttribute("localEntity", localEntity);
            model.addAttribute("adminItemEntity", adminItemEntity);

            return "adminhub/localDetail";
        } else {
            // LocalEntity를 찾지 못한 경우 처리
            return "adminhub/localDetail"; // 또는 적절한 오류 뷰 반환
        }
    }
    private String determineContentType(String locationType) {
        if (locationType == null) {
            return "기타"; // 기본적으로 처리할 contentType 설정
        }

        // 'locationType' 매개변수를 기반으로 contentType 결정하는 로직 구현
        // if-else나 switch 문을 사용하여 'locationType'을 contentType으로 매핑
        // 예를 들어:
        if (locationType.equals("명소")) {
            return "명소";
        } else if (locationType.equals("식당")) {
            return "식당";
        } else if (locationType.equals("숙박")) {
            return "숙박";
        } else {
            // 알 수 없는 contentType 처리 또는 기본값 설정
            return "기타";
        }
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
    @ResponseBody
    public ResponseEntity<String> createAdminItem(@ModelAttribute AdminItemDto adminItemDto,
                                                  @RequestParam("Mon") String Mon,
                                                  @RequestParam("Tue") String Tue,
                                                  @RequestParam("Wed") String Wed,
                                                  @RequestParam("Thu") String Thu,
                                                  @RequestParam("Fri") String Fri,
                                                  @RequestParam("Sat") String Sat,
                                                  @RequestParam("Sun") String Sun,
                                                  @RequestParam("contentType") String contentType,
                                                  @RequestParam("imageFile") MultipartFile imageFile,
                                                  Long localEntityId,
                                                  Model model) {
        try {
            // 필수 항목이 비어 있는지 확인
            if (adminItemDto.getTouristSpotName().isEmpty() || adminItemDto.getAddress().isEmpty() || adminItemDto.getContact().isEmpty() ||
                    adminItemDto.getFeatures().isEmpty()) {
                return ResponseEntity.badRequest().body("모든 필수 항목을 입력해주세요.");
            }

            try {
                // 이미지 저장
                String imgUrl = saveImage(imageFile);
                adminItemDto.setImgUrl(imgUrl);

                adminItemDto.setBusinessHours("Mon", Mon);
                adminItemDto.setBusinessHours("Tue", Tue);
                adminItemDto.setBusinessHours("Wed", Wed);
                adminItemDto.setBusinessHours("Thu", Thu);
                adminItemDto.setBusinessHours("Fri", Fri);
                adminItemDto.setBusinessHours("Sat", Sat);
                adminItemDto.setBusinessHours("Sun", Sun);

                adminItemDto.setContentType(contentType); // contentType 설정
                adminItemService.saveAdminItem(adminItemDto, localEntityId);
            } catch (Exception e) {
                e.printStackTrace(); // 혹은 다른 로깅 방식으로 오류를 기록할 수 있습니다.
            }
            return ResponseEntity.ok().body("상품이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            // 오류 발생 시 오류 응답 반환
            logger.error("상품 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
        }
    }


    @DeleteMapping("/item/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        try {
            adminItemService.deleteItem(id);
            return ResponseEntity.ok("상품이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("상품 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 삭제에 실패했습니다.");
        }
    }

    @PostMapping("/item/{id}")
    public String getItemDetails(@PathVariable Long id, Model model) {

        // itemId를 사용하여 데이터베이스에서 해당 아이템 정보를 조회
        AdminItemEntity item = adminItemService.findById(id);

        // 모델에 아이템 정보를 추가하여 View로 전달
        model.addAttribute("item", item);

        // 해당 아이템의 세부 정보를 렌더링할 Thymeleaf 템플릿 이름을 반환
        return "adminhub/localDetailShow"; // 실제로 사용할 Thymeleaf 템플릿의 이름으로 수정
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
