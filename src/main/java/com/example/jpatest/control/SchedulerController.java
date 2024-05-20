package com.example.jpatest.control;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.entity.Member;
import com.example.jpatest.entity.Scheduler;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.repository.SchedulerRepository;
import com.example.jpatest.service.AdminItemService;
import com.example.jpatest.service.GoogleMapsService;
import com.example.jpatest.service.MemberService;
import com.example.jpatest.service.SchedulerService;
import com.example.jpatest.util.GeneticAlgorithmTSP;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/schedulers")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final LocalRepository localRepository;
    private final GoogleMapsService googleMapsService;
    private final AdminItemService adminItemService;
    private final SchedulerRepository schedulerRepository;
    private final MemberService memberService;
    /*private final GeneticAlgorithmTSP geneticAlgorithmTSP;*/
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @GetMapping("/first")
    public String first(Model model, HttpSession session) {
        SchedulerDto schedulerDto = new SchedulerDto();
        session.setAttribute("schedulerDto", schedulerDto);
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/first";
    }

    @PostMapping("/second")
    public String second(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto, Model model, HttpSession session) {
        try {
            List<LocalEntity> localEntities = localRepository.findAll();

            // 조회된 데이터를 모델에 추가
            model.addAttribute("localEntities", localEntities);
            model.addAttribute("schedulerDto", schedulerDto);
            session.setAttribute("schedulerDto", schedulerDto);
            logger.info("Received schedulerDto: {}", schedulerDto.getTrip_duration_end());

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
                    model.addAttribute("initItemEntitySpot", adminItemEntityList2);
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
                         @RequestParam("contentType") String contentTypes,
                         Model model, HttpSession session) {
        /*/////////////////////////추가///////////////////////////////*/
        try {
            String stayIds = (String) session.getAttribute("stayIds");
            if (stayIds != null) {
                model.addAttribute("stayIds", stayIds);
                String[] stayIdArray = stayIds.split(",");
                if (stayIdArray.length > 0) {
                    List<AdminItemEntity> adminItemEntityStay = new ArrayList<>();
                    for (String stayId : stayIdArray) {
                        AdminItemEntity itemsForStayId = adminItemService.findById(Long.parseLong(stayId));
                        adminItemEntityStay.add(itemsForStayId);
                    }
                    model.addAttribute("initItemEntityStay", adminItemEntityStay);
                }
            }
            /*/////////////////////////추가///////////////////////////////*/
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
            session.setAttribute("contentTypes", contentTypes);

            System.out.println(localIds);
            System.out.println(spotIds);
            System.out.println("--------------------------!!");
            System.out.println(contentTypes);

            return "scheduler/fourth";
        }catch(Exception e){
            System.out.println("Error occurred: " + e.getMessage());
            return "redirect:/third";
        }
    }
    @PostMapping("/result")
    public String result(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                         @RequestParam("stayId") String stayIds,
                         @RequestParam("stayMark") String stayMarks,
                         @RequestParam("StartAirport") String StartAirport,
                         @RequestParam("EndAirport") String EndAirport,
                         Model model, HttpSession session) {
            schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");

            /*----------------------------------------------------------------------------------*/
            // 폼에서 시간과 분을 추출합니다.
            int Shour = Integer.parseInt(schedulerDto.getDepartureHour());
            int Sminute = Integer.parseInt(schedulerDto.getDepartureMinute());
            int Ehour = Integer.parseInt(schedulerDto.getArrivalHour());
            int Eminute = Integer.parseInt(schedulerDto.getArrivalMinute());

            String durationStart = schedulerDto.getTrip_duration_start();
            String durationEnd = schedulerDto.getTrip_duration_end();

            System.out.println("durationStart : "+durationStart);
            System.out.println("durationEnd : "+durationEnd);
            // 정규 표현식을 사용하여 월과 일을 추출
            Pattern pattern = Pattern.compile("(\\d+)월 (\\d+)일");
            Matcher startMatcher = pattern.matcher(durationStart);
            Matcher endMatcher = pattern.matcher(durationEnd);

            if (startMatcher.find() && endMatcher.find() ) {
                // 추출된 월과 일을 정수형으로 변환
                int Smonth = Integer.parseInt(startMatcher.group(1));
                int Sday = Integer.parseInt(startMatcher.group(2));
                int Emonth = Integer.parseInt(endMatcher.group(1));
                int Eday = Integer.parseInt(endMatcher.group(2));

                // 입력된 시간과 분으로 LocalDateTime 객체를 생성합니다.
                LocalDateTime startDateTime = LocalDateTime.of(2024, Smonth, Sday, Shour, Sminute);
                GeneticAlgorithmTSP.setOriginStartTime(startDateTime);

                LocalDateTime endDateTime = LocalDateTime.of(2024, Emonth, Eday, Ehour, Eminute);
                GeneticAlgorithmTSP.setOriginEndTime(endDateTime);
            }
            /*----------------------------------------------------------------------------------*/

            // 세션에서 localIds, spotIds 가져오기
            String localIds = (String) session.getAttribute("localIds");
            String spotIds = (String) session.getAttribute("spotIds");
            String spotMarks = (String) session.getAttribute("spotMarks");


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
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            List<AdminItemEntity> day1Routes = new ArrayList<>(); List<AdminItemEntity> day2Routes = new ArrayList<>(); List<AdminItemEntity> day3Routes = new ArrayList<>();
            List<AdminItemEntity> day4Routes = new ArrayList<>(); List<AdminItemEntity> day5Routes = new ArrayList<>(); List<AdminItemEntity> day6Routes = new ArrayList<>();
            List<AdminItemEntity> day7Routes = new ArrayList<>(); List<AdminItemEntity> day8Routes = new ArrayList<>(); List<AdminItemEntity> day9Routes = new ArrayList<>();
            List<AdminItemEntity> day10Routes = new ArrayList<>();
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//            String origin="인천광역시 중구 공항로424번길 47"; String destination="인천광역시 중구 공항로424번길 47";

            List<SchedulerDto> routes = googleMapsService.getOptimalRoute(StartAirport,EndAirport, filteredAdminItems);
            System.out.println(routes);
            /*System.out.println(airport);*/

            String[] combinedArray = new String[stayIdArray.length + spotIdArray.length];
            System.arraycopy(stayIdArray, 0, combinedArray, 0, stayIdArray.length);
            System.arraycopy(spotIdArray, 0, combinedArray, stayIdArray.length, spotIdArray.length);
            List<AdminItemEntity> reFilteredAdminItems = new ArrayList<>();
            addSchedulersByStartLocations(reFilteredAdminItems, StartAirport);

            for(int i=1; i < routes.size(); i++){
                for(int j=0; j < combinedArray.length; j++) {
                    if(Long.parseLong(combinedArray[j]) == routes.get(i).getResultItemId() ){
                        reFilteredAdminItems.add(googleMapsService.getScheduler(Long.parseLong(combinedArray[j])));
                    }
                }
            }
            addSchedulersByEndLocations(reFilteredAdminItems,EndAirport);


            System.out.println("reAdmins Size : "+ reFilteredAdminItems.size());
            System.out.println("routes Size : "+ routes.size());
            LocalDate currentDate = routes.get(0).getArrivalTime().toLocalDate();
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue();
            int day = currentDate.getDayOfMonth(); // day 4.25일
            int i=0; int day0=0, day1=0, day2=0, day3=0, day4=0, day5=0, day6=0, day7=0, day8=0, day9=0;
            System.out.println("startDay : " + day);
            for(SchedulerDto route : routes){
                //( i == 0) {i++; continue;}
                //if( i == (routes.size()-1) ) break;
                LocalDate dateTemp = route.getArrivalTime().toLocalDate();
                int yearTemp = dateTemp.getYear();
                int dayTemp = dateTemp.getDayOfMonth();
                int diff;
                //System.out.println("nextDay : " + dayTemp);
                if (dayTemp >= day) { //dayTemp 5월 1일
                    diff = dayTemp - day;
                 } else  {
                    YearMonth yearMonthObject = YearMonth.of(year, month);
                    int daysInMonth = yearMonthObject.lengthOfMonth(); // 해당 월의 날짜 수
                    diff = daysInMonth + dayTemp - day;
                }

                switch(diff) {
                    case 0:
                        day1Routes.add(reFilteredAdminItems.get(i));
                        i++; day0++;
                        break;
                    case 1:
                        day2Routes.add(reFilteredAdminItems.get(i));
                        i++; day1++;
                        break;
                    case 2:
                        day3Routes.add(reFilteredAdminItems.get(i));
                        i++; day2++;
                        break;
                    case 3:
                        day4Routes.add(reFilteredAdminItems.get(i));
                        i++; day3++;
                        break;
                    case 4:
                        day5Routes.add(reFilteredAdminItems.get(i));
                        i++; day4++;
                        break;
                    case 5:
                        day6Routes.add(reFilteredAdminItems.get(i));
                        i++; day5++;
                        break;
                    case 6:
                        day7Routes.add(reFilteredAdminItems.get(i));
                        i++; day6++;
                        break;
                    case 7:
                        day8Routes.add(reFilteredAdminItems.get(i));
                        i++; day7++;
                        break;
                    case 8:
                        day9Routes.add(reFilteredAdminItems.get(i));
                        i++; day8++;
                        break;
                    case 9:
                        day10Routes.add(reFilteredAdminItems.get(i));
                        i++; day9++;
                        break;
                }
            }
            List<SchedulerDto> route1 = new ArrayList<>(); List<SchedulerDto> route2 = new ArrayList<>(); List<SchedulerDto> route3 = new ArrayList<>();
            List<SchedulerDto> route4 = new ArrayList<>(); List<SchedulerDto> route5 = new ArrayList<>(); List<SchedulerDto> route6 = new ArrayList<>();
            List<SchedulerDto> route7 = new ArrayList<>(); List<SchedulerDto> route8 = new ArrayList<>(); List<SchedulerDto> route9 = new ArrayList<>();
            List<SchedulerDto> route10 = new ArrayList<>();
            int k = 0;
            for (;k < day0; k++) {
                route1.add(routes.get(k));
            }
            for (; k < day1+day0; k++) {
                route2.add(routes.get(k));
            }
            for (; k < day0+day1+day2; k++) {
                route3.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3; k++) {
                route4.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4; k++) {
                route5.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4+day5; k++) {
                route6.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4+day5+day6; k++) {
                route7.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4+day5+day6+day7; k++) {
                route8.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4+day5+day6+day7+day8; k++) {
                route9.add(routes.get(k));
            }
            for (; k < day0+day1+day2+day3+day4+day5+day6+day7+day8+day9; k++) {
                route10.add(routes.get(k));
            }



            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            session.setAttribute("stayIds", stayIds);
            session.setAttribute("stayMarks", stayMarks);
            session.setAttribute("StartAirport", StartAirport);
            session.setAttribute("EndAirport", EndAirport);
            model.addAttribute("adminItemEntity0", day1Routes);
            model.addAttribute("adminItemEntity1", day2Routes);
            model.addAttribute("adminItemEntity2", day3Routes);
            model.addAttribute("adminItemEntity3", day4Routes);
            model.addAttribute("adminItemEntity4", day5Routes);
            model.addAttribute("adminItemEntity5", day6Routes);
            model.addAttribute("adminItemEntity6", day7Routes);
            model.addAttribute("adminItemEntity7", day8Routes);
            model.addAttribute("adminItemEntity8", day9Routes);
            model.addAttribute("adminItemEntity9", day10Routes);

            //model.addAttribute("adminItemEntity", filteredAdminItems);
            model.addAttribute("schedulerDto", schedulerDto);
            System.out.println(route2);
            model.addAttribute("route1", route1); model.addAttribute("route2", route2); model.addAttribute("route3", route3);
            model.addAttribute("route4", route4); model.addAttribute("route5", route5); model.addAttribute("route6", route6);
            model.addAttribute("route7", route7); model.addAttribute("route18", route8); model.addAttribute("route9", route9);
            model.addAttribute("route10", route10);


            return "scheduler/result";
    }

    @PostMapping("/save")
    public String save(Model model, HttpSession session, Principal principal) {
        // 현재 로그인한 사용자의 정보를 가져옵니다.
        if (principal == null) {
            // 로그인되지 않은 사용자인 경우 처리
            return "redirect:/members/login"; // 로그인 페이지로 리다이렉트
        }

        // 현재 로그인한 사용자의 정보를 가져옵니다.
        String loggedInUsername = principal.getName();
        Member loggedInMember = memberService.findByEmail(loggedInUsername);

        String localIds = (String) session.getAttribute("localIds");
        String spotIds = (String) session.getAttribute("spotIds");
        String spotMarks = (String) session.getAttribute("spotMarks");
        String stayIds = (String) session.getAttribute("stayIds");
        String stayMarks = (String) session.getAttribute("stayMarks");
        SchedulerDto schedule = (SchedulerDto) session.getAttribute("schedulerDto");

        // SchedulerDto에서 필요한 데이터를 가져와서 엔티티에 저장
        String departureHour = schedule.getDepartureHour();
        String departureMinute = schedule.getDepartureMinute();
        String arrivalHour = schedule.getArrivalHour();
        String arrivalMinute = schedule.getArrivalMinute();
        String tripDurationStart = schedule.getTrip_duration_start();
        String tripDurationEnd = schedule.getTrip_duration_end();

        // Scheduler 객체 생성 및 현재 로그인한 사용자의 Member 객체 설정
        Scheduler scheduler = new Scheduler();
        scheduler.setLocalIds(localIds);
        scheduler.setSpotIds(spotIds);
        scheduler.setSpotMarks(spotMarks);
        scheduler.setStayIds(stayIds);
        scheduler.setStayMarks(stayMarks);
        scheduler.setDepartureHour(departureHour);
        scheduler.setDepartureMinute(departureMinute);
        scheduler.setArrivalHour(arrivalHour);
        scheduler.setArrivalMinute(arrivalMinute);
        scheduler.setTrip_duration_start(tripDurationStart);
        scheduler.setTrip_duration_end(tripDurationEnd);
        scheduler.setSchedulerMemberId(loggedInMember); // 현재 로그인한 사용자의 Member 객체 설정


        System.out.println(spotMarks);
        // SchedulerRepository를 사용하여 엔티티를 저장
        schedulerRepository.save(scheduler);

        // 세션 비우기
        session.invalidate();

        return "redirect:/";
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
            System.out.println(schedulerDto.getTrip_duration_end());
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
//        SchedulerDto schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");
//        String localIds = (String) session.getAttribute("localIds");
//        String spotIds = (String) session.getAttribute("spotIds");
//        String spotMarks = (String) session.getAttribute("spotMarks");
//        String contentTypes = (String) session.getAttribute("contentTypes");
//        model.addAttribute("schedulerDto", schedulerDto);
//        model.addAttribute("localIds", localIds);

        try {
            //---------------------------추가-------------------------------------
//            String[] localIdArray = localIds.split(",");
//            String[] itemIdArray = spotIds.split(",");
//            List<Long> itemIdList = Arrays.stream(itemIdArray)
//                    .map(Long::parseLong)
//                    .collect(Collectors.toList());
//
//            List<AdminItemEntity> initialItems = adminItemService.findByIds(itemIdList); // 에러발생
//
//            if (initialItems != null && !initialItems.isEmpty()) {
//                model.addAttribute("initialItemSpot", initialItems); // LocalEntity 객체 목록을 추가합니다.
//            }
//            //---------------------------추가-------------------------------------
//
//            // localIdArray의 첫 번째 localId로 초기 지도 좌표 설정
//            if (localIdArray.length > 0) {
//                Long firstLocalId = Long.parseLong(localIdArray[0]);
//                LocalEntity initialLocal = localRepository.findById(firstLocalId).orElse(null);
//
//                if (initialLocal != null) {
//                    // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
//                    model.addAttribute("initialLocal", initialLocal); // 전체 LocalEntity 객체를 추가합니다.
//                }
//            }
//
//            // 각 localId에 해당하는 관련 데이터를 가져와서 모델에 추가합니다.
//            List<AdminItemEntity> adminItemEntityList = new ArrayList<>();
//            for (String localId : localIdArray) {
//                List<AdminItemEntity> itemsForLocalId = adminItemService.findBylistId(Long.parseLong(localId));
//                adminItemEntityList.addAll(itemsForLocalId);
//            }
//            System.out.println("------------------------------------------------------");
//            System.out.println(spotMarks);
//            // 모델에 데이터를 추가합니다.
//            model.addAttribute("adminItemEntity", adminItemEntityList);
//
//            // 세션에 localIds를 저장합니다.
//            session.setAttribute("localIds", localIds); // localIds는 쉼표(,)로 구분된 문자열입니다.
//
//            session.setAttribute("spotMarks", spotMarks); // localIds는 쉼표(,)로 구분된 문자열입니다.
//
//            session.setAttribute("contentTypes", contentTypes); // localIds는 쉼표(,)로 구분된 문자열입니다.
            session.invalidate();
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
        String stayIds = (String) session.getAttribute("stayIds");
        String stayMarks = (String) session.getAttribute("stayMarks");
        model.addAttribute("schedulerDto", schedulerDto);
        model.addAttribute("localIds", localIds);

        try {
            //---------------------------추가-------------------------------------
            String[] localIdArray = localIds.split(",");
            String[] itemIdArray = stayIds.split(",");
            List<Long> itemIdList = Arrays.stream(itemIdArray)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<AdminItemEntity> initialItems = adminItemService.findByIds(itemIdList); // 에러발생

            if (initialItems != null && !initialItems.isEmpty()) {
                // initialLocal 정보를 기반으로 지도의 초기 좌표 설정
                model.addAttribute("initialItemStay", initialItems); // LocalEntity 객체 목록을 추가합니다.
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
            System.out.println("------------------------------------------------------");
            System.out.println(stayMarks);
            // 모델에 데이터를 추가합니다.
            model.addAttribute("adminItemEntity", adminItemEntityList);


            // 세션에 localIds를 저장합니다.
            session.setAttribute("localIds", localIds); // localIds는 쉼표(,)로 구분된 문자열입니다.
            session.setAttribute("stayMarks", stayMarks); // localIds는 쉼표(,)로 구분된 문자열입니다.

            return "scheduler/fourth";
        } catch (Exception e) {
            // 오류 발생 시 예외 처리
            System.out.println("Error occurred: " + e.getMessage());
            return "redirect:/third";
        }
    }

    public void addSchedulersByStartLocations(List<AdminItemEntity> reFilteredAdminItems, String StartAirport){
        if(StartAirport.contains("인천광역시")) reFilteredAdminItems.add(googleMapsService.getScheduler(191L));
        if(StartAirport.contains("청원구 내수읍")) reFilteredAdminItems.add(googleMapsService.getScheduler(192L));
        if(StartAirport.contains("공항로")) reFilteredAdminItems.add(googleMapsService.getScheduler(193L));
        if(StartAirport.contains("강서구 공항진입로") )reFilteredAdminItems.add(googleMapsService.getScheduler(194L));
        if(StartAirport.contains("하늘길") )reFilteredAdminItems.add(googleMapsService.getScheduler(195L));
        if(StartAirport.contains("오타구 하네다쿠코") )reFilteredAdminItems.add(googleMapsService.getScheduler(209L));
    }
    public void addSchedulersByEndLocations(List<AdminItemEntity> reFilteredAdminItems, String EndAirport){
        if(EndAirport.contains("인천광역시")) reFilteredAdminItems.add(googleMapsService.getScheduler(191L));
        if(EndAirport.contains("청원구 내수읍")) reFilteredAdminItems.add(googleMapsService.getScheduler(192L));
        if(EndAirport.contains("공항로")) reFilteredAdminItems.add(googleMapsService.getScheduler(193L));
        if(EndAirport.contains("강서구 공항진입로")) reFilteredAdminItems.add(googleMapsService.getScheduler(194L));
        if(EndAirport.contains("하늘길") )reFilteredAdminItems.add(googleMapsService.getScheduler(195L));
        if(EndAirport.contains("오타구 하네다쿠코") )reFilteredAdminItems.add(googleMapsService.getScheduler(209L));
    }

}