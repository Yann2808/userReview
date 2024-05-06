package fr.saysa.userReview.repository;

import fr.saysa.userReview.entity.JWT;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<JWT, Integer> {
}
