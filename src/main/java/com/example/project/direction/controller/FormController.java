package com.example.project.direction.controller;

import com.example.project.direction.dto.InputDto;
import com.example.project.pharmacy.service.PharmacyRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class FormController {

    private final PharmacyRecommendationService pharmacyRecommendationService;

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @PostMapping("/search")
    public ModelAndView postDirection(@ModelAttribute InputDto inputDto) { // @ModelAttribute 기본생성자(디폴트 생성자)가 있으면 setter가 필요하고 파라미터가 있는 생성자가 있으면 setter가 필요없다.

        ModelAndView modelAndView = new ModelAndView(); // 전달할 화면과 값을 담아서 전달
        modelAndView.setViewName("output");
        modelAndView.addObject("outputFormList", pharmacyRecommendationService.recommendPharmacyList(inputDto.getAddress()));

        return modelAndView;
    }
}
