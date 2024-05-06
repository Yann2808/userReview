package fr.saysa.userReview.repository;

import fr.saysa.userReview.entity.JWT;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JwtRepository extends CrudRepository<JWT, Integer> {
    Optional<JWT> findByValue(String value);
}
