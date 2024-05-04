package fr.saysa.userReview.service;

import fr.saysa.userReview.entity.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void envoyer(Validation validation) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@saysa.bj");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre code d'activation");

        String texte = String.format(
                "Bonjour %s, <br /> Votre code d'activation est %s; A bientôt",
                validation.getUtilisateur().getNom(),
                validation.getCode()
                );
        mailMessage.setText(texte);

        // Envoi du mail
        javaMailSender.send(mailMessage);
    }
}
