package com.tove.ws_project.service;

import com.tove.ws_project.model.Game;
import com.tove.ws_project.model.GameApi;
import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
            return reviewRepository.save(review);
        } else {
            GameApi gameApi = apiService.getGame(gameId).block();
            Game game = gameRepository.save(gameApi.toGame());
            review.setGame(game);
            return reviewRepository.save(review);

        }
    }
}
