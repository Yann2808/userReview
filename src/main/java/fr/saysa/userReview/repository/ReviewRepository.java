package fr.saysa.userReview.repository;

import fr.saysa.userReview.entity.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
}
