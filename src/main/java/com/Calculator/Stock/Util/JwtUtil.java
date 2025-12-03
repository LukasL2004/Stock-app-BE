package com.Calculator.Stock.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "AceastaEsteOCheieSecretaExtremDeLungaPentruAFiSuficientDeSiguraPeste512DeBitidsdsdsda21431rfdf242fdfasfr2432rrgfdfer2ffddffddf";
    private final Long JWT_TOKEN_VALIDITY = 1000L * 60 * 60 * 10;

    // FUNCȚIA TA ORIGINALĂ - Generează token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)  // Nou API: .subject() în loc de .setSubject()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey())  // Nou API
                .compact();
    }

    // Extrage username din token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrage data expirării
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrage orice claim din token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // IMPORTANT: Pentru versiuni noi de jjwt (0.11.x / 0.12.x)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // Sau parserBuilder() - ambele funcționează
                .verifyWith(getSigningKey())  // Nou API
                .build()
                .parseSignedClaims(token)  // Nou API: parseSignedClaims în loc de parseClaimsJws
                .getPayload();  // Nou API: getPayload în loc de getBody
    }

    // Verifică dacă token-ul a expirat
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validează token pentru un user specific
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Validare simplă
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // Generează token cu claims extra
    public String generateTokenWithClaims(String email, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)  // Nou API
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey())
                .compact();
    }

    // Pentru Spring Security UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateTokenWithClaims(userDetails.getUsername(), claims);
    }

    // Convertește SECRET_KEY în SecretKey pentru noul API
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(SECRET_KEY.getBytes())
        );
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}