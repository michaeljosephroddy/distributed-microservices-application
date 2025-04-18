package com.example.api_gateway;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/token")
    public Map<String, String> token(@RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client) {
        return Map.of("access_token", client.getAccessToken().getTokenValue());
    }
}
