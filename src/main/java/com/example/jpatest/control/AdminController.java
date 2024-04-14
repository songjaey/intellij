package com.example.jpatest.control;

import com.example.jpatest.dto.AdminLocalDto;
import com.example.jpatest.entity.AdminLocalEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.repository.AdminLocalRepository;
import com.example.jpatest.repository.LocalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private AdminLocalRepository adminLocalRepository;

    @GetMapping("/first")
    public String itemForm(Model model){  //상품 작성 페이지 제공
        return "adminhub/first";
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

    @GetMapping("/localDetail")
    public String showLocalDetail(@RequestParam("country") String country,
                                  @RequestParam("local") String local) {
        // 로컬 상세 페이지를 처리하는 로직을 구현하세요
        return "adminhub/localDetail"; // 해당 뷰 템플릿의 이름을 반환합니다
    }

    @GetMapping("/eventCustom")
    public String eventCustom(Model model){  //상품 작성 페이지 제공
        return "adminhub/eventCustom";
    }

    @PostMapping("/second")
    public String handleLocalDetailForm(@ModelAttribute AdminLocalDto adminLocalDto,
                                        @RequestParam("imageFile") MultipartFile imageFile,
                                        RedirectAttributes redirectAttributes) {

        try {
            // 이미지 파일 업로드 처리
            if (!imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                adminLocalDto.setImageFile(fileName); // 이미지 파일명 설정
            }

            // AdminLocalDto를 AdminLocalEntity로 변환
            AdminLocalEntity localEntity = convertDtoToEntity(adminLocalDto);

            // 데이터베이스에 저장
            adminLocalRepository.save(localEntity);

            // 저장 성공 메시지 설정
            redirectAttributes.addFlashAttribute("message", "Local 정보가 성공적으로 저장되었습니다.");

            // 페이지 리다이렉트
            return "redirect:/admin/first"; // 저장 후 이동할 페이지 경로를 지정합니다
        } catch (Exception e) {
            logger.error("Local 정보 저장 중 오류 발생: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Local 정보 저장 중 오류가 발생했습니다.");
            return "redirect:/admin/first"; // 오류 발생 시 다시 상품 작성 페이지로 이동합니다
        }
    }

    private AdminLocalEntity convertDtoToEntity(AdminLocalDto adminLocalDto) {
        AdminLocalEntity entity = new AdminLocalEntity();
        // AdminLocalDto에서 필요한 데이터를 AdminLocalEntity에 설정
        entity.setTouristSpotName(adminLocalDto.getTouristSpotName());
        entity.setAddress(adminLocalDto.getAddress());
        entity.setPhoneNumber(adminLocalDto.getPhoneNumber());
        entity.setOpeningHours(adminLocalDto.getOpeningHours());
        // 필요한 다른 정보 설정

        return entity;
    }
}
