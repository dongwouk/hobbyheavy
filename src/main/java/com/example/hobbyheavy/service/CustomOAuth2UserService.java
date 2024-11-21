package com.example.hobbyheavy.service;

import com.example.hobbyheavy.oauth2.CustomOAuth2User;
import com.example.hobbyheavy.oauth2.GoogleResponse;
import com.example.hobbyheavy.oauth2.NaverResponse;
import com.example.hobbyheavy.oauth2.OAuth2Response;
import com.example.hobbyheavy.oauth2.OAuth2UserResponse;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User); // 값 확인

        // NAVER,GOOGLE 둘 중 어디서 온 요청인지 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String userId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        System.out.println(userId);
        Optional<User> user = userRepository.findByUserId(userId);

        // 우리서비스에 로그인을 하지 않았던 사용자
        if (user.isEmpty()) {

            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setUsername(oAuth2Response.getUsername());
            newUser.setEmail(oAuth2Response.getEmail());
            newUser.setRole(Collections.singleton(Role.ROLE_USER));
            newUser.setAge(20);
            newUser.setGender(true);
            newUser.setPassword("12345678");

            userRepository.save(newUser);

            OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse();
            oAuth2UserResponse.setUserId(userId);
            oAuth2UserResponse.setUsername(oAuth2Response.getUsername());
            oAuth2UserResponse.setRole("ROLE_USER");

            return new CustomOAuth2User(oAuth2UserResponse);
        }
        else { // 우리서비스에 로그인을 했던 사용자
            User existUser = user.get();
            existUser.setEmail(oAuth2Response.getEmail());
            existUser.setUsername(oAuth2Response.getUsername());

            userRepository.save(existUser);

            OAuth2UserResponse oAuth2UserResponse = new OAuth2UserResponse();
            oAuth2UserResponse.setUserId(existUser.getUserId());
            oAuth2UserResponse.setUsername(oAuth2Response.getUsername());
            oAuth2UserResponse.setRole(existUser.getRole().toString());

            return new CustomOAuth2User(oAuth2UserResponse);
        }

    }
}
