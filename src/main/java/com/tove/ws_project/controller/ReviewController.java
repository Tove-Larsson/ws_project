package com.tove.ws_project.controller;

import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.ReviewRepository;
import com.tove.ws_project.service.ReviewService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PutMapping("/update/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            Review existingReview = review.get();
            review.get().setTitle((String) requestBody.get("title"));
            review.get().setContent((String) requestBody.get("content"));
            reviewRepository.saveAndFlush(review.get());
            return ResponseEntity.ok(existingReview);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/findall/{gameId}")
    public ResponseEntity<List<Review>> getReviewsByGameId(@PathVariable("gameId") Integer gameId) {

        Optional<List<Review>> reviews = reviewRepository.findByGame_Id(gameId);

        return reviews.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable("id") UUID id) {

        Optional<Review> review = reviewRepository.findById(id);

        return review.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
