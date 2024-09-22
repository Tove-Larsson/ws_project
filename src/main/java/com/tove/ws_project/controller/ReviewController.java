package com.tove.ws_project.controller;

import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.ReviewRepository;
import com.tove.ws_project.service.ReviewService;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<Object> createReview(@RequestBody Map<String, Object> requestBody) {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {

            Object gameIdObj = requestBody.get("gameId");
            if (!(gameIdObj instanceof Integer) || (Integer)gameIdObj < 0) {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", 400);
                responseBody.put("errors", List.of("gameId: must be a valid signed integer."));
                return ResponseEntity.badRequest().body(responseBody);
            }

        Integer gameId = (Integer) requestBody.get("gameId");
        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");

        Review review = new Review(title, content);

            Validator validator = factory.getValidator();

            Set<ConstraintViolation<Review>> violations = validator.validate(review);
            if (!violations.isEmpty()) {
                List<String> errorMessages = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.toList());
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", 400);
                responseBody.put("errors", errorMessages);
                return ResponseEntity.badRequest().body(responseBody);
            }

            review = reviewService.saveReview(review, gameId);
            return ResponseEntity.status(201).body(review);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
            // "An error occurred while saving the review."
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") UUID id) {

        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            reviewRepository.deleteById(id);
            return ResponseEntity.noContent().build();
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
