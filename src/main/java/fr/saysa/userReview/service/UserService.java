package fr.saysa.userReview.service;

import fr.saysa.userReview.RoleType;
import fr.saysa.userReview.entity.Role;
import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.entity.Validation;
import fr.saysa.userReview.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    // Injection dans les services
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ValidationService validationService;

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

        utilisateur = this.userRepository.save(utilisateur);

        this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireEnFonctionDuCode(activation.get("code"));
        if(Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Votre code a expiré.");
        }
        Utilisateur utilisateurActiver =
                this.userRepository.findById(validation.getUtilisateur().getId())
                        .orElseThrow(() -> new RuntimeException("Votre utilisateur n'existe pas"));

        utilisateurActiver.setActive(true);
        this.userRepository.save(utilisateurActiver);
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond à cet identifiant."));
    }
}
