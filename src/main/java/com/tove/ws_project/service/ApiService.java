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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    private long parseDateToTimestamp(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    }

    public Mono<List<GameApi>> getGames(String title, String minDate, String maxDate) {
        String url = "https://api.igdb.com/v4/games/";

        StringBuilder requestBodyBuilder = new StringBuilder("fields *; limit 100; ");

        requestBodyBuilder.append("search \"").append(title).append("\"; ");

        requestBodyBuilder.append("where category = 0");

        if (minDate != null && !minDate.isEmpty()) {
            System.out.println(parseDateToTimestamp(minDate));
            requestBodyBuilder.append(" & first_release_date >= ").append(parseDateToTimestamp(minDate));
        }
        if (maxDate != null && !maxDate.isEmpty()) {
            System.out.println(parseDateToTimestamp(maxDate));
            requestBodyBuilder.append(" & first_release_date <= ").append(parseDateToTimestamp(maxDate));
        }

        requestBodyBuilder.append(";");

        System.out.println(requestBodyBuilder);

        String requestBody = requestBodyBuilder.toString();

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
