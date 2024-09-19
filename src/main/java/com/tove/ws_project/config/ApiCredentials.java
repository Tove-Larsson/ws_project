package com.tove.ws_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiCredentials {

    @Value("${twitch.client-id}")
    private String clientId;

    @Value("${igdb.access-token}")
    private String accessToken;

    public String getClientId() {
        return clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
