package com.example.jpatest.control;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.entity.Scheduler;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.service.AdminItemService;
import com.example.jpatest.service.GoogleMapsService;
import com.example.jpatest.service.SchedulerService;
import com.google.maps.model.DirectionsRoute;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/schedulers")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final LocalRepository localRepository;

    private final AdminItemService adminItemService;
    private final GoogleMapsService googleMapsService;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @GetMapping("/first")
    public String first(Model model, HttpSession session) {
        SchedulerDto schedulerDto = new SchedulerDto();
        session.setAttribute("schedulerDto", schedulerDto);
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/first";
    }

    @PostMapping("/second")
    public String second(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto, Model model,HttpSession session) {
        try {
            List<LocalEntity> localEntities = localRepository.findAll();

            // 조회된 데이터를 모델에 추가
            model.addAttribute("localEntities", localEntities);
            model.addAttribute("schedulerDto", schedulerDto);
            session.setAttribute("schedulerDto", schedulerDto);
            logger.info("Received schedulerDto: {}", schedulerDto.getTrip_duration_end());

//            System.out.println("--------------------------------------------------------");
//            System.out.println(schedulerDto.getTrip_duration_end());

            // 두 번째 페이지로 리다이렉트
            return "scheduler/second";
        }catch(Exception e){
            System.out.println("Error occurred: " + e.getMessage());
            return "redirect:/first";
        }
    }

    @PostMapping("/third")
    public String Third(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                        @RequestParam("localId") String localIds, Model model,
                        HttpSession session) {
        try {
            /////////////////////////추가///////////////////////////////
            String spotIds = (String) session.getAttribute("spotIds");
            if (spotIds != null) {
                // localIds 세션이 존재할 때만 JavaScript 코드를 모델에 추가
                model.addAttribute("spotIds", spotIds);
                String[] spotIdArray = spotIds.split(",");
                if (spotIdArray.length > 0) {
                    List<AdminItemEntity> adminItemEntityList2 = new ArrayList<>();
                    for (String spotId : spotIdArray) {
                        AdminItemEntity itemsForSpotId = adminItemService.findById(Long.parseLong(spotId));
                        adminItemEntityList2.add(itemsForSpotId);
                    }
                    model.addAttribute("initItemEntity2", adminItemEntityList2);
                }
            }
            /////////////////////////추가///////////////////////////////
            schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");
            // localIds를 쉼표(,)로 분할하여 각 localId를 추출합니다.
            String[] localIdArray = localIds.split(",");

            // localIdArray의 첫 번째 localId로 초기 지도 좌표 설정
            if (localIdArray.length > 0) {
                Long firstLocalId = Long.parseLong(localIdArray[0]);
                LocalEntity initialLocal = localRepository.findById(firstLocalId).orElse(null);

                if (initialLocal != null) {
                    // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                    model.addAttribute("initialLocal", initialLocal); // 전체 LocalEntity 객체를 추가합니다.
                }
            }

            // 각 localId에 해당하는 관련 데이터를 가져와서 모델에 추가합니다.
            List<AdminItemEntity> adminItemEntityList = new ArrayList<>();
            for (String localId : localIdArray) {
                List<AdminItemEntity> itemsForLocalId = adminItemService.findBylistId(Long.parseLong(localId));
                adminItemEntityList.addAll(itemsForLocalId);
            }

            // 모델에 데이터를 추가합니다.
            model.addAttribute("adminItemEntity", adminItemEntityList);
            model.addAttribute("schedulerDto", schedulerDto);

            // 세션에 저장합니다.
            session.setAttribute("localIds", localIds); // localIds는 쉼표(,)로 구분된 문자열입니다.
            session.setAttribute("schedulerDto", schedulerDto);

            //System.out.println(localIds);
            //System.out.println(schedulerDto.getTrip_duration_end());

            return "scheduler/third";
        } catch (Exception e) {
            // 오류 발생 시 예외 처리
            System.out.println("Error occurred: " + e.getMessage());
            return "redirect:/second";
        }
    }

    @PostMapping("/fourth")
    public String Fourth(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                         @RequestParam("spotId") String spotIds,
                         @RequestParam("spotMark") String spotMarks,
                         Model model, HttpSession session) {
        schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");

        // 세션에서 localIds 배열을 가져옴
        String localIds = (String) session.getAttribute("localIds");
        String[] localIdArray = localIds.split(",");

        // localIdArray의 첫 번째 localId로 초기 지도 좌표 설정
        if (localIdArray.length > 0) {
            Long firstLocalId = Long.parseLong(localIdArray[0]);
            LocalEntity initialLocal = localRepository.findById(firstLocalId).orElse(null);

            if (initialLocal != null) {
                // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                model.addAttribute("initialLocal", initialLocal); // 전체 LocalEntity 객체를 추가합니다.
            }
        }

        // 각 localId에 대해 관련 데이터를 가져오고 모델에 추가
        List<AdminItemEntity> adminItemEntityList = new ArrayList<>();
        for (String localId : localIdArray) {
            List<AdminItemEntity> itemsForLocalId = adminItemService.findBylistId(Long.parseLong(localId));
            adminItemEntityList.addAll(itemsForLocalId);
        }

        // 모델에 데이터 추가
        model.addAttribute("adminItemEntity", adminItemEntityList);
        model.addAttribute("schedulerDto", schedulerDto);

        // 세션에 저장합니다.
        session.setAttribute("schedulerDto", schedulerDto);
        session.setAttribute("spotIds", spotIds);
        session.setAttribute("spotMarks", spotMarks);

        //System.out.println(localIds);
        //System.out.println(spotIds);
        //System.out.println(schedulerDto.getTrip_duration_end());

        return "scheduler/fourth";

    }

    @PostMapping("/result")
    public String result(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                         @RequestParam("stayId") String stayIds,
                         @RequestParam("stayMark") String stayMarks,
                         Model model, HttpSession session) {

        schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");

        // 세션에서 localIds, spotIds 가져오기
        String localIds = (String) session.getAttribute("localIds");
        String spotIds = (String) session.getAttribute("spotIds");

        // localIds 배열로 변환
        String[] localIdArray = localIds.split(",");

        // 첫 번째 localId로 초기 지도 좌표 설정
        if (localIdArray.length > 0) {
            Long firstLocalId = Long.parseLong(localIdArray[0]);
            LocalEntity initialLocal = localRepository.findById(firstLocalId).orElse(null);

            if (initialLocal != null) {
                // 지도 초기 좌표 설정
                model.addAttribute("initialLocal", initialLocal);
            }
        }

        // stayIds와 spotIds를 배열로 분할하여 필터링된 adminItemEntity 리스트 준비
        List<AdminItemEntity> filteredAdminItems = new ArrayList<>();

        // stayIds 필터링
        String[] stayIdArray = stayIds.split(",");
        for (String stayId : stayIdArray) {
            Long stayIdValue = Long.parseLong(stayId);
            AdminItemEntity itemForStayId = adminItemService.findById(stayIdValue);
            if (itemForStayId != null && itemForStayId.getId().equals(stayIdValue)) {
                filteredAdminItems.add(itemForStayId);
            }
        }

        // spotIds 필터링
        String[] spotIdArray = spotIds.split(",");
        for (String spotId : spotIdArray) {
            Long spotIdValue = Long.parseLong(spotId);
            AdminItemEntity itemForSpotId = adminItemService.findById(spotIdValue);
            if (itemForSpotId != null && itemForSpotId.getId().equals(spotIdValue)) {
                filteredAdminItems.add(itemForSpotId);
            }
        }

        String origin="인천광역시 중구 공항로424번길 47"; String destination="인천광역시 중구 공항로424번길 47";

        DirectionsRoute[] routes = googleMapsService.getOptimalRoute(origin, destination, filteredAdminItems);

        if (routes != null && routes.length > 0) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < routes.length; i++) {
                result.append("Route ").append(i + 1).append(": ");
                result.append("Distance: ").append(routes[i].legs[0].distance).append(", ");
                result.append("Duration: ").append(routes[i].legs[0].duration).append("<br>");
            }
        }
        session.setAttribute("stayMarks", stayMarks);
        model.addAttribute("adminItemEntity", filteredAdminItems);
        model.addAttribute("schedulerDto", schedulerDto);

        return "scheduler/result";
    }

    @GetMapping("/second")
    public String getSecondPage(Model model, HttpSession session) {
        SchedulerDto schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");
        String localIds = (String) session.getAttribute("localIds");

        model.addAttribute("schedulerDto", schedulerDto);
        model.addAttribute("localIds", localIds);
        try {
            // --------------------------추가 ------------------------------//
            String[] localIdArray = localIds.split(",");
            List<Long> localIdList = Arrays.stream(localIdArray)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<LocalEntity> initialLocals = localRepository.findByIds(localIdList);

            // initialLocals가 null이 아니고 비어있지 않은지 확인
            if (initialLocals != null && !initialLocals.isEmpty()) {
                // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                model.addAttribute("initialLocals", initialLocals); // LocalEntity 객체 목록을 추가합니다.
            }
            List<LocalEntity> localEntities = localRepository.findAll();

            // 조회된 데이터를 모델에 추가
            model.addAttribute("localEntities", localEntities);
            model.addAttribute("schedulerDto", schedulerDto);
            // schedulerDto 저장
            /*        schedulerService.saveScheduler(schedulerDto);*/
            //System.out.println(schedulerDto.getTrip_duration_end());
            logger.info("Received schedulerDto: {}", schedulerDto.getTrip_duration_end());

            // 두 번째 페이지로 리다이렉트
            return "scheduler/second";
        }catch(Exception e){
            System.out.println("Error occurred: " + e.getMessage());
            return "scheduler/second";
        }

    }
    @GetMapping("/third")
    public String getThirdPage(Model model, HttpSession session) {
        SchedulerDto schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");
        String localIds = (String) session.getAttribute("localIds");
        String spotIds = (String) session.getAttribute("spotIds");
        model.addAttribute("schedulerDto", schedulerDto);
        model.addAttribute("localIds", localIds);

        try {
            //---------------------------추가-------------------------------------
            String[] localIdArray = localIds.split(",");
            String[] itemIdArray = spotIds.split(",");
            List<Long> itemIdList = Arrays.stream(itemIdArray)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<AdminItemEntity> initialItems = adminItemService.findByIds(itemIdList); // 에러발생

            if (initialItems != null && !initialItems.isEmpty()) {
                // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                model.addAttribute("initialItems", initialItems); // LocalEntity 객체 목록을 추가합니다.
            }
            //---------------------------추가-------------------------------------


            // localIdArray의 첫 번째 localId로 초기 지도 좌표 설정
            if (localIdArray.length > 0) {
                Long firstLocalId = Long.parseLong(localIdArray[0]);
                LocalEntity initialLocal = localRepository.findById(firstLocalId).orElse(null);

                if (initialLocal != null) {
                    // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                    model.addAttribute("initialLocal", initialLocal); // 전체 LocalEntity 객체를 추가합니다.
                }
            }

            // 각 localId에 해당하는 관련 데이터를 가져와서 모델에 추가합니다.
            List<AdminItemEntity> adminItemEntityList = new ArrayList<>();
            for (String localId : localIdArray) {
                List<AdminItemEntity> itemsForLocalId = adminItemService.findBylistId(Long.parseLong(localId));
                adminItemEntityList.addAll(itemsForLocalId);
            }

            // 모델에 데이터를 추가합니다.
            model.addAttribute("adminItemEntity", adminItemEntityList);

            // 세션에 localIds를 저장합니다.
            session.setAttribute("localIds", localIds); // localIds는 쉼표(,)로 구분된 문자열입니다.

            return "scheduler/third";
        } catch (Exception e) {
            // 오류 발생 시 예외 처리
            System.out.println("Error occurred: " + e.getMessage());
            return "redirect:/second";
        }
    }
    @GetMapping("/fourth")
    public String getFourthPage(Model model, HttpSession session) {
        SchedulerDto schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");
        String localIds = (String) session.getAttribute("localIds");
        String spotIds = (String) session.getAttribute("spotIds");
        model.addAttribute("schedulerDto", schedulerDto);
        model.addAttribute("localIds", localIds);
        model.addAttribute("spotIds", spotIds);
        return "scheduler/fourth";
    }


}