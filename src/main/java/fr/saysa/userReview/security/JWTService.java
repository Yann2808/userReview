package fr.saysa.userReview.security;

import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@Service
public class JWTService {

    private final String ENCRYPTION_KEY = "80d6070adcf001df01a80383d6e17f37396c8fac469a238a6ebf03b782399a31";

    private UserService userService;

    public Map<String, String> generate (String username) {
        Utilisateur utilisateur = this.userService.loadUserByUsername(username);
        return this.generateJet(utilisateur);
    }

    private Map<String, String> generateJet(Utilisateur utilisateur) {

        final Map<String, String> claims = Map.of(
                utilisateur.getNom(),
                utilisateur.getEmail()
        );
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30 * 60 * 1000;

        // Jwts déprécié sur la version 0.12.5, je suis revenu sur la 0.11.2
        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of("bearer", bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }
}
