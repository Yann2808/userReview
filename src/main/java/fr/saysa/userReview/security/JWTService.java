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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JWTService {

    public static final String BEARER = "bearer";
    private final String ENCRYPTION_KEY = "80d6070adcf001df01a80383d6e17f37396c8fac469a238a6ebf03b782399a31";

    // Les injections
    private UserService userService;
    private JwtRepository jwtRepository;

    public JWT tokenByValue(String value) {
        return this.jwtRepository.findByValue(value)
                .orElseThrow(() -> new RuntimeException("Utilisateur inconnu"));
    }

    final long currentTime = System.currentTimeMillis();
    final long expirationTime = currentTime + 30 * 60 * 1000;

    public String extractUsername(String token) {
        return this.getClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = getClaims(token, Claims::getExpiration);

        return expirationDate.before(new Date());
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
        final Map<String, String> jwtMap = this.generateJet(utilisateur);

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
}
