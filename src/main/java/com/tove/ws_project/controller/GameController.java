package com.tove.ws_project.controller;

import com.tove.ws_project.model.Game;
import com.tove.ws_project.model.GameApi;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    private final ApiService apiService;
    private final GameRepository gameRepository;

    @Autowired
    public GameController(ApiService apiService, GameRepository gameRepository) {
        this.apiService = apiService;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/search/{title}")
    public Mono<ResponseEntity<List<GameApi>>> getGames(@PathVariable("title") String title) {
        return apiService.getGames(title)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(
                        ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                ));
    }

    @PostMapping("/db/addgames")
    public ResponseEntity<List<Game>> addGames(@RequestBody List<Game> games) {
        try {
            List<Game> savedGames = gameRepository.saveAll(games);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGames);
        } catch (Exception e) {
            System.err.println("Error creating game: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
