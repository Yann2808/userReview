package fr.saysa.userReview.controller;

import fr.saysa.userReview.dto.AuthentificationDTO;
import fr.saysa.userReview.entity.Utilisateur;
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

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur user) {
        log.info("Inscription");
        this.userService.inscription(user);
    }

    @PostMapping(path = "activation")
    public void activation(@RequestBody Map<String, String> activation) {
        this.userService.activation(activation);
    }

    @PostMapping(path = "connexion")
    public Map<String, String> connexion(AuthentificationDTO authentificationDTO) {
        final Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );
        log.info("Résultat : " + authenticate.isAuthenticated());
        return null;
    }
}
