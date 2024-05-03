package fr.saysa.userReview.controller;

import fr.saysa.userReview.entity.Review;
import fr.saysa.userReview.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("reviews")
@RestController
public class ReviewController {
    private final ReviewService reviewService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void creer(@RequestBody Review review) {
        this.reviewService.createReview(review);
    }
}
