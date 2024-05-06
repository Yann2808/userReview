package fr.saysa.userReview.security;

import fr.saysa.userReview.entity.JWT;
import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.repository.JwtRepository;
import fr.saysa.userReview.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Transactional
@AllArgsConstructor
@Service
public class JWTService {

    public static final String BEARER = "bearer";
    private static final Logger log = LoggerFactory.getLogger(JWTService.class);
    private final String ENCRYPTION_KEY = "80d6070adcf001df01a80383d6e17f37396c8fac469a238a6ebf03b782399a31";

    // Les injections
    private UserService userService;
    private JwtRepository jwtRepository;

    final long currentTime = System.currentTimeMillis();
    final long expirationTime = currentTime + 30 * 60 * 1000;

    public String extractUsername(String token) {
        return this.getClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getClaims(token, Claims::getExpiration);

        return expirationDate.before(new Date());
    }

    public JWT tokenByValue(String value) {
        return this.jwtRepository.findByValueAndDesactiveAndExpire(
                value,
                        false,
                        false)
                .orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
    }

    private <T> T getClaims (String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> generate (String username) {
        Utilisateur utilisateur = this.userService.loadUserByUsername(username);
        this.disableTokens(utilisateur);
        final Map<String, String> jwtMap = new HashMap<>(this.generateJet(utilisateur));

        final JWT jwt = JWT
                .builder()
                .value(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .build();

        this.jwtRepository.save(jwt);
        return jwtMap;
    }

    private void disableTokens(Utilisateur utilisateur) {
        final List<JWT> jwtList = this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
    }

    private Map<String, String> generateJet(Utilisateur utilisateur) {

        final Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        // Jwts déprécié sur la version 0.12.5, je suis revenu sur la 0.11.2
        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return Map.of(BEARER, bearer);
    }

    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JWT jwt = this.jwtRepository.findUtilisateurValidToken
                (utilisateur.getEmail(),
                        false,
                        false
                ).orElseThrow(() -> new RuntimeException("Token invalide"));

        jwt.setExpire(true);
        jwt.setDesactive(true);
        this.jwtRepository.save(jwt);
    }

    @Scheduled(cron = "@daily")
    public void removeUselessJwt() {
        log.info("Suppression du token à {}", Instant.now());
        this.jwtRepository.deleteAllByExpireAndDesactive(true, true);
}
}
