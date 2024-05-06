package fr.saysa.userReview.controller;

import fr.saysa.userReview.dto.AuthenticationDTO;
import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.security.JWTService;
import fr.saysa.userReview.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private AuthenticationManager authenticationManager;
    private final UserService userService;
    private JWTService jwtService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur user) {
        log.info("Inscription");
        this.userService.inscription(user);
    }

    // Pour l'activation du compte créé en utilisant le code d'activation qui lui a été envoyé par mail
    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        this.userService.activation(activation);
    }

    // Pour la déconnexion du compte de l'utilisateur
    @PostMapping(path = "deconnexion")
    public void deconnexion() {
        this.jwtService.deconnexion();
    }

    @PostMapping(path = "connexion")
    public Map<String, String> connexion(@RequestBody AuthenticationDTO authenticationDTO) {
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
        );

        if(authenticate.isAuthenticated()) {
            return this.jwtService.generate(authenticationDTO.username());
        }
        log.info("Résultat : " + authenticate.isAuthenticated());
        return null;
    }
}
