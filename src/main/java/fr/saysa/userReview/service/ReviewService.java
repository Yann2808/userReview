package fr.saysa.userReview.service;

import fr.saysa.userReview.entity.Review;
import fr.saysa.userReview.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void createReview(Review review) {
        this.reviewRepository.save(review);
    }
}
