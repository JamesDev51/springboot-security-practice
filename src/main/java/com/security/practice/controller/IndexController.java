package com.security.practice.controller;

import com.security.practice.config.auth.PrincipalDetails;
import com.security.practice.model.User;
import com.security.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
        //2가지 방 법
        //DI(의존성 주입)
        System.out.println("/test/login ==============");
        //Authentication -> Principal (Object 타입 )->  PrincipalDetails로 캐스팅 -> 받아옴
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication : "+principalDetails.getUser());

        System.out.println("userDetails : "+userDetails.getUser());

        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(Authentication authentication,@AuthenticationPrincipal OAuth2User oAuth2){
        //2가지 방 법
        //DI(의존성 주입)
        System.out.println("/test/login ==============");
        //Authentication -> Principal (Object 타입 )->  PrincipalDetails로 캐스팅 -> 받아옴
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication : "+oAuth2User.getAttributes());
        System.out.println("oAuth2User" + oAuth2.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }


    @GetMapping({"","/"})
    public String index(){
        //머스테치 기본 폴더  src/main/resources/
        //뷰 리졸버 설정 : template (prefix), .mustache(suffix)
        return "index";
    }

    //Oauth 로그인을 해도 PrincipalDetails 타입으로 받을 수 있고
    //일반 로그인을 해도 PrincipalDetails 타입으로 받을 수 있다.
    @GetMapping("/user")
    public  @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("PrincipalDetials : "+ principalDetails.getUser());
        return "user";
    }
    @GetMapping("/admin")
    public  @ResponseBody String admin(){
        return "admin";
    }
    @GetMapping("/manager")
    public  @ResponseBody String manager(){
        return "manager";
    }
    @GetMapping("/loginForm")
    public  String loginForm(){
        return "loginForm";
    }
    @GetMapping("/joinForm")
    public  String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public  String joinProc(User user){
        user.setRole("ROLE_USER");
        String rawPassword=user.getPassword();
        String encPassword=bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); //회원가입이 잘됨 -> but 비밀번호가 암호화
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보 : ";
    }
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //함수 실행전에 수행 -> 얘는 hasRole 문법을 사용할 수 있음
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터 정보 : ";
    }


}
