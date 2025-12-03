package com.Calculator.Stock.Config;

import com.Calculator.Stock.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class Jwtauthenticationfilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // PASUL 1: Extrage header-ul "Authorization"
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // PASUL 2: Verifică dacă header-ul există și începe cu "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extrage token-ul (elimină "Bearer " din față)
            jwt = authorizationHeader.substring(7);

            try {
                // PASUL 3: Extrage email-ul din token
                email = jwtUtil.extractUsername(jwt);

            } catch (ExpiredJwtException e) {
                // Token-ul a expirat
                System.out.println("JWT Token has expired: " + e.getMessage());

            } catch (Exception e) {
                // Eroare la parsarea token-ului
                System.out.println("Error parsing JWT Token: " + e.getMessage());
            }
        }

        // PASUL 4: Dacă am email și nu există deja autentificare
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // PASUL 5: Încarcă user-ul din DB
            // CustomUserDetailsService va folosi findByEmail()
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // PASUL 6: Validează token-ul
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // PASUL 7: Creează obiectul de autentificare
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // PASUL 8: Setează autentificarea în SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                System.out.println("User '" + email + "' authenticated successfully via JWT");
            }
        }

        // PASUL 9: Continuă cu următorul filtru
        filterChain.doFilter(request, response);
    }
}
