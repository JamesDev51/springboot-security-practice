package com.security.practice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"","/"})
    public String index(){
        //머스테치 기본 폴더  src/main/resources/
        //뷰 리졸버 설정 : template (prefix), .mustache(suffix)
        return "index";
    }
}
