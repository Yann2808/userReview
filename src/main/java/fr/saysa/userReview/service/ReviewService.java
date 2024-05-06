package fr.saysa.userReview.service;

import fr.saysa.userReview.entity.Review;
import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void createReview(Review review) {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        review.setUtilisateur(utilisateur);
        this.reviewRepository.save(review);
    }
}
