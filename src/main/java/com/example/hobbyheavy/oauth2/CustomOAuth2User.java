package com.example.hobbyheavy.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserResponse oAuth2UserResponse;

    public CustomOAuth2User(OAuth2UserResponse oAuth2UserResponse) {

        this.oAuth2UserResponse = oAuth2UserResponse;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                // Role 값에서 대괄호 제거
                return oAuth2UserResponse.getRole().replace("[", "").replace("]", "");
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return oAuth2UserResponse.getUsername();
    }

    public String getUserId() {

        return oAuth2UserResponse.getUserId();
    }
}
