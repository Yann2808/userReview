package fr.saysa.userReview.controller;

import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur user) {
        log.info("Inscription");
        this.userService.inscription(user);
    }
}
