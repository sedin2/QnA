package com.sedin.qna.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final String BEARER = "Bearer ";
    private static final String ROLES = "roles";
    private static final Long TOKEN_EXPIRE_TIME = 1000L * 60 * 30;

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String encode(String email, List<GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        String authorities = roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(ROLES, authorities);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    public Authentication decode(String token) {
        String email = getUserEmail(token);
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) getRoles(token);
        return new UsernamePasswordAuthenticationToken(email, "", authorities);
    }

    public String getUserEmail(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public Collection<? extends GrantedAuthority> getRoles(String token) {
        Claims claims = getClaims(token);
        return Arrays.stream(claims.get(ROLES).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExistedToken(String token) {
        if (token == null) {
            return false;
        }
        return true;
    }

    public boolean isBearerScheme(String token) {
        if (token.startsWith(BEARER)) {
            return true;
        }
        return false;
    }

    public String resolveToken(String token) {
        return token.substring(BEARER.length());
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
