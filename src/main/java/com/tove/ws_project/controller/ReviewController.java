package com.tove.ws_project.controller;

import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.ReviewRepository;
import com.tove.ws_project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/create")
    public Review createReview(@RequestBody Map<String, Object> requestBody) {

        Integer gameId = (Integer) requestBody.get("gameId");

        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");
        Review review = new Review(title, content);

        return reviewService.saveReview(review, gameId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") UUID id) {

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
