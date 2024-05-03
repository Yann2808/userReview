package fr.saysa.userReview.repository;

import fr.saysa.userReview.entity.Utilisateur;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<Utilisateur, Integer> {
        Optional<Utilisateur> findByEmail(String email);
}
