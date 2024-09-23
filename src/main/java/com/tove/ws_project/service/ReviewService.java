package com.tove.ws_project.service;

import com.tove.ws_project.model.Game;
import com.tove.ws_project.model.GameApi;
import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class ReviewService {

    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final ApiService apiService;

    @Autowired
    public ReviewService(GameRepository gameRepository, ReviewRepository reviewRepository, ApiService apiService) {
        this.gameRepository = gameRepository;
        this.reviewRepository = reviewRepository;
        this.apiService = apiService;
    }

    public Review saveReview(Review review, long gameId) {

        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            review.setGame(game);
        } else {
            try {
                GameApi gameApi = apiService.getGame(gameId).block();
                assert gameApi != null;
                Game game = gameRepository.save(gameApi.toGame());
                review.setGame(game);
            } catch (RuntimeException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found or external API error.");
            }

        }
        return reviewRepository.save(review);
    }
}
