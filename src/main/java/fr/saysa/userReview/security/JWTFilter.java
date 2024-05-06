package fr.saysa.userReview.security;

import fr.saysa.userReview.entity.Utilisateur;
import fr.saysa.userReview.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JWTFilter extends OncePerRequestFilter {
    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */

    private UserService userService;
    private JWTService jwtService;

    public JWTFilter(UserService userService, JWTService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String username = null;
        boolean isTokenExpired = true;

        final String authorization = request.getHeader("Authorization");

        // Récupérer le jeton à partir de l'index 8 du champ Authorization
        if (authorization != null && authorization.startsWith("Bearer ")) {
            jwt = authorization.substring(7);
            isTokenExpired = jwtService.isTokenExpired(jwt);
            username = jwtService.extractUsername(jwt);
        }

        if(!isTokenExpired && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken AuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(AuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
