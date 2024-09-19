package com.tove.ws_project.service;

import com.tove.ws_project.model.Game;
import com.tove.ws_project.model.GameApi;
import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Review addReview(Long id, String title, String content) {

        Game game = gameRepository.findById(id).orElseGet(() -> {

            GameApi gameApi = apiService.getGame(id).block();
            if (gameApi == null) {
                throw new RuntimeException("Game not found in IGDB");
            }
            Game newGame = new Game(
                    (long) gameApi.getId(),
                    gameApi.getName(),
                    gameApi.getStoryline(),
                    gameApi.getRating()
            );
            return gameRepository.save(newGame);
        });

        Review review = new Review();
        review.setTitle(title);
        review.setContent(content);
        review.setGame(game);

        return reviewRepository.save(review);
    }

}
