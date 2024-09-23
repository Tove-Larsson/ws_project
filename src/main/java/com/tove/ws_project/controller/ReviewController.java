package com.tove.ws_project.controller;

import com.tove.ws_project.exception.InvalidGameIdException;
import com.tove.ws_project.exception.ResourceNotFoundException;
import com.tove.ws_project.exception.ValidationException;
import com.tove.ws_project.model.Game;
import com.tove.ws_project.model.Review;
import com.tove.ws_project.repository.GameRepository;
import com.tove.ws_project.repository.ReviewRepository;
import com.tove.ws_project.service.ReviewService;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository, GameRepository gameRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createReview(@RequestBody Map<String, Object> requestBody) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {

            Object gameIdObj = requestBody.get("gameId");
            if (!(gameIdObj instanceof Integer) || (Integer) gameIdObj < 0) {
                throw new InvalidGameIdException("gameId: must be a valid signed integer.");
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
                throw new ValidationException(errorMessages);
            }

            review = reviewService.saveReview(review, gameId);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") UUID id) {
        Optional<Review> review = reviewRepository.findById(id);

        if (review.isPresent()) {
            reviewRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        throw new ResourceNotFoundException("Review not found for the given ID.");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateReview(@PathVariable("id") UUID id, @RequestBody Map<String, Object> requestBody) {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {

            Optional<Review> optionalReview = reviewRepository.findById(id);

            if (optionalReview.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found for the given ID.");
            }

            Review review = optionalReview.get();
            review.setTitle((String) requestBody.get("title"));
            review.setContent((String) requestBody.get("content"));

            Validator validator = factory.getValidator();

            Set<ConstraintViolation<Review>> violations = validator.validate(review);
            if (!violations.isEmpty()) {
                List<String> errorMessages = violations.stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .collect(Collectors.toList());
                throw new ValidationException(errorMessages);
            }

            reviewRepository.saveAndFlush(review);
            return ResponseEntity.ok(review);

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/findall/{gameId}")
    public ResponseEntity<List<Review>> getReviewsByGameId(@PathVariable("gameId") Long gameId) {
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            throw new ResourceNotFoundException("No review found for the given game id");
        }

        List<Review> reviews = reviewRepository.findByGame_Id(gameId).orElseGet(Collections::emptyList);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable("id") UUID id) {
        Optional<Review> review = reviewRepository.findById(id);

        if (review.isEmpty()) {
            throw new ResourceNotFoundException("Review not found for the given ID.");
        }

        return ResponseEntity.ok(review.get());
    }

}
