package com.security.practice.config.auth.oauth;

import com.security.practice.config.auth.PrincipalDetails;
import com.security.practice.config.auth.oauth.provider.FacebookUserInfo;
import com.security.practice.config.auth.oauth.provider.GoogleUserInfo;
import com.security.practice.config.auth.oauth.provider.OAuth2UserInfo;
import com.security.practice.model.User;
import com.security.practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    public PrincipalOauth2UserService(@Lazy  BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    //구글로 부터 받은 userRequest 데이터에 대한  후처리 되는 함수
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest : "+userRequest.getClientRegistration()); //registrationId 로 어떤 Oauth로 로그인 했는지 확인 가능
        System.out.println("getAccessToken : "+userRequest.getAccessToken().getTokenValue());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code 를 리턴 ->(OAuth-Client 라이브러리) -> AccessToken 요청
        //여기까지가 userRequest 정보 ->회원 프로필을 받아야 함(loadUser 함수 홈수) ->구글로부터 회원프로필 받아준다.
        System.out.println("getAttributes : "+oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
            System.out.println("페이스북 로그인 요청");

        }else{
            System.out.println("우리는 구글과 페이스북만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId= (String) oAuth2UserInfo.getProviderId(); //1231124
        String username=provider+"_"+providerId; //google_1231124 ->  유저네임 충돌 없음
        String email = oAuth2User.getAttribute("email");
        String password= bCryptPasswordEncoder.encode("아무비밀번호");
        String role="ROLE_USER";

        User userEntity=userRepository.findByUsername(username);
        if(userEntity==null){
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerIid(providerId)
                    .build();
            userRepository.save(userEntity);
        }
        return new PrincipalDetails(userEntity,oAuth2User.getAttributes());
    }
}
