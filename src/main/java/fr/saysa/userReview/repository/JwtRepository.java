package fr.saysa.userReview.repository;

import fr.saysa.userReview.entity.JWT;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<JWT, Integer> {
    Optional<JWT> findByValueAndDesactiveAndExpire(String value, boolean desactive, boolean expire);

    @Query("FROM JWT j WHERE j.expire = :expire AND j.desactive = :desactive AND j.utilisateur.email = :email")
    Optional<JWT> findUtilisateurValidToken(String email, boolean desactive, boolean expire);

    @Query("FROM JWT j WHERE j.utilisateur.email = :email")
    Stream<JWT> findUtilisateur(String email);

    //@Query("FROM JWT j WHERE j.refreshToken.valeur = :valeur")
    //Optional<JWT> findByRefreshToken(String valeur);

    void deleteAllByExpireAndDesactive(boolean expire, boolean desactive);

}
