package com.example.oauth2;

import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class SimpleController {

    @GetMapping("/api/session")
    public Authentication session() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/")
    public Authentication index(Authentication authentication){
        return authentication;
    }

    @GetMapping("/callback")
    public OAuth2Token callback(@RequestParam(name = "code", required = false) String code){
        final RestTemplate restTemplate = new RestTemplate();
        final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("grant_type", "authorization_code");
        parameters.add("redirect_uri", "http://localhost:8080/callback");
        parameters.add("scopes", "read_profile");
        parameters.add("scopes", "read_email");
        parameters.add("scopes", "read_address");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("client", "password");

        //권한 서버에서 발급 받은 code 값으로 다시 권한 서버에 token 정보를 요청한다.
        final ResponseEntity<OAuth2Token> stringResponseEntity = restTemplate.postForEntity(
                "http://localhost:8080/oauth/token",
                new HttpEntity<>(parameters, headers),
                OAuth2Token.class
        );

        return stringResponseEntity.getBody();

    }

    @Getter
    public static class OAuth2Token {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private String expires_in;
        private String scope;

    }
}
