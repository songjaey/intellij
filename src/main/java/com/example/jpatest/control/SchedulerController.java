package com.example.jpatest.control;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.entity.AdminItemEntity;
import com.example.jpatest.entity.LocalEntity;
import com.example.jpatest.entity.Scheduler;
import com.example.jpatest.repository.LocalRepository;
import com.example.jpatest.service.AdminItemService;
import com.example.jpatest.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/schedulers")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final LocalRepository localRepository;

    private final AdminItemService adminItemService;
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
        session.setAttribute("schedulerDto", schedulerDto);
        // 모든 LocalEntity 조회
        List<LocalEntity> localEntities = localRepository.findAll();

        // 조회된 데이터를 모델에 추가
        model.addAttribute("localEntities", localEntities);
        model.addAttribute("schedulerDto", schedulerDto);
        // schedulerDto 저장
        /*        schedulerService.saveScheduler(schedulerDto);*/

        // 두 번째 페이지로 리다이렉트
        return "scheduler/second";
    }


    @PostMapping("/third")
    public String Third(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                        @RequestParam("localId") String localId, Model model,
                        HttpSession session) {
        schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");

        model.addAttribute("schedulerDto", schedulerDto);

        // 리스트에 데이터 추가
        List<AdminItemEntity> adminItemEntity = adminItemService.findBylistId(Long.parseLong(localId));


        // 모델에 리스트 추가
        model.addAttribute("adminItemEntity", adminItemEntity);


        return "scheduler/third";
    }

    @PostMapping("/fifth")
    public String fifth(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto,
                        Model model, HttpSession session) {
        schedulerDto = (SchedulerDto) session.getAttribute("schedulerDto");

        model.addAttribute("schedulerDto", schedulerDto);

        // 리스트에 데이터 추가
//        List<AdminItemEntity> adminItemEntity = adminItemService.findBylistId(Long.parseLong(localId));


        // 모델에 리스트 추가
//        model.addAttribute("adminItemEntity", adminItemEntity);


        return "scheduler/fifth";
    }



}