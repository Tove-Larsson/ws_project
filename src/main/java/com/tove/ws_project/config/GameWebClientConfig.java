package com.tove.ws_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GameWebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient
                .builder()
                .baseUrl("https://api.igdb.com/v4");
    }


}
