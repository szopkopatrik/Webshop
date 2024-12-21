package com.inn.webshop.com.inn.webshop.service;

import com.inn.webshop.com.inn.webshop.data.entity.RoleEntity;
import com.inn.webshop.com.inn.webshop.data.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    UserService customerDetailsService;

    public static final String SECRET = "t7zu7zgzuhgtgzttzugttz7t7ztt7ztz67ftz7tz7frz67tfrrzt6fr5tdrfzgzughui8g7zfr67ztfrz67tftz6fd67zfrtdt67zfd67zfd67zgzt";
    public String extractUsername(String token) {
        try {
            String username = extractClaim(token, Claims::getSubject);
            System.out.println("Extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.out.println("Error extracting username: " + e.getMessage());
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails, int id, RoleEntity role) {
        Map<String, Object> claims = new HashMap<>();
        userDetails.getAuthorities().forEach(authority -> claims.put(authority.getAuthority(), authority));

        claims.put("id", id);
        claims.put("role", role.getRoleName());
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+600000))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            boolean isTokenValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            System.out.println("Is token valid? " + isTokenValid);
            return isTokenValid;
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        Date date = extractClaim(token, Claims::getExpiration);
        return date.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        final Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claimsResolvers.apply(claims);
    }
}