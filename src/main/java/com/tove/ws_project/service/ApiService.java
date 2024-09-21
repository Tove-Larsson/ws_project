package com.tove.ws_project.service;

import com.tove.ws_project.config.ApiCredentials;
import com.tove.ws_project.model.GameApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApiService {

    private final ApiCredentials apiCredentials;
    private final WebClient webClient;

    @Autowired
    public ApiService(ApiCredentials apiCredentials, WebClient.Builder webClientBuilder) {
        this.apiCredentials = apiCredentials;
        this.webClient = webClientBuilder.build();
    }

    public Mono<List<GameApi>> getGames(String title) {
        String url = "https://api.igdb.com/v4/games/";

        String requestBody = String.format("fields *; limit 100; where category = 0; search \"%s\";",title);

        return webClient.post()
                .uri(url)
                .header("Client-ID", apiCredentials.getClientId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiCredentials.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("Client Error: " + clientResponse.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("Server Error: " + clientResponse.statusCode())))
                .bodyToFlux(GameApi.class)
                .collectList();

    }

    public Mono<GameApi> getGame(Long id) {
        String url = "https://api.igdb.com/v4/games";

        String requestBody = String.format("fields *; where id = %d;", id);
        return webClient.post()
                .uri(url)
                .header("Client-ID", apiCredentials.getClientId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiCredentials.getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("Client Error: " + clientResponse.statusCode()))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("Server Error: " + clientResponse.statusCode()))
                )
                .bodyToMono(new ParameterizedTypeReference<List<GameApi>>() {})
                .flatMap(games -> {
                    if (games != null && !games.isEmpty()) {
                        return Mono.just(games.get(0));
                    } else {
                        return Mono.error(new RuntimeException("No games found"));
                    }
                });
    }



}
