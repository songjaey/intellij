package com.example.jpatest.control;

import com.example.jpatest.dto.SchedulerDto;
import com.example.jpatest.service.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/schedulers")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @GetMapping("/first")
    public String first(Model model) {
        SchedulerDto schedulerDto = new SchedulerDto();
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/first";
    }

    @PostMapping("/second")
    public String second(@ModelAttribute("schedulerDto") SchedulerDto schedulerDto) {
        schedulerService.saveScheduler(schedulerDto);
        logger.info("Received schedulerDto: {}", schedulerDto);

        return "scheduler/second"; // 두 번째 페이지로 리다이렉트
    }

    @GetMapping("/third")
    public String getThird(Model model) {
        SchedulerDto schedulerDto = new SchedulerDto();
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/third";
    }

    @PostMapping("/third")
    public String postThird(Model model) {
        SchedulerDto schedulerDto = new SchedulerDto();
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/third";
    }

    @GetMapping("/fifth")
    public String getFifth(Model model) {
        SchedulerDto schedulerDto = new SchedulerDto();
        model.addAttribute("schedulerDto", schedulerDto);
        return "scheduler/third";
    }



}