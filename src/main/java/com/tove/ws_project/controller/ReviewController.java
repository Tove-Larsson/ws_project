package com.tove.ws_project.controller;

import com.tove.ws_project.model.Review;
import com.tove.ws_project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    public Review createReview(@RequestBody Map<String, Object> requestBody) {

        Integer gameId = (Integer) requestBody.get("gameId");

        String title = (String) requestBody.get("title");
        String content = (String) requestBody.get("content");
        Review review = new Review(title, content);

        return reviewService.saveReview(review, gameId);
    }

}
