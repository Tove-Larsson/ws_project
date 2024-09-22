package com.tove.ws_project.controller;

import com.tove.ws_project.model.GameApi;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final ApiService apiService;
    @Autowired
    public GameController(ApiService apiService, GameRepository gameRepository) {
        this.apiService = apiService;
    }

    @GetMapping("/search/{title}")
    public Mono<ResponseEntity<List<GameApi>>> getGames(@PathVariable("title") String title,
                                                        @RequestParam(required = false) String maxDate,
                                                        @RequestParam(required = false) String minDate) {
        return apiService.getGames(title, minDate, maxDate)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    if (error instanceof DateTimeParseException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                    }
                });
    }

}
