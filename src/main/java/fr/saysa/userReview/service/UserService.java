package fr.saysa.userReview.service;

import fr.saysa.userReview.RoleType;
import fr.saysa.userReview.entity.Role;
import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    public void inscription(Utilisateur utilisateur) {

        if(!utilisateur.getEmail().contains("@")) {
            throw new RuntimeException("Votre mail est invalide");
        }

        Optional<Utilisateur> utilisateurOptional = this.userRepository.findByEmail(utilisateur.getEmail());

        // Vérifier si le mail de l'utilisateur est déjà utilisé
        if(utilisateurOptional.isPresent()) {
            throw new RuntimeException("Votre email est déjà utilisé");
        }

        String mdpCrypte = this.passwordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(mdpCrypte);

        Role roleUtilisateur = new Role();
        roleUtilisateur.setRole(RoleType.USER);
        utilisateur.setRole(roleUtilisateur);

        this.userRepository.save(utilisateur);

    }
}
