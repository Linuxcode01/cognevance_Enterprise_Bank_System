package com.chandan.enterprise_banking_transaction_system.config;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

public class JWTAuthConfig {

    private final static String SECRET = "thismySuperSecretKeyForEnterpriseBankingSystem2026!938937";
    private final static Key key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public static String stripBearer(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    public static String accessToken(String username){
        return Jwts.builder()
                .claim("type","access")
                .subject(username)
                .signWith(key, SignatureAlgorithm.HS384)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .compact();
    }

    public static String refreshToken(String username){
        return Jwts.builder()
                .claim("type","refresh")
                .subject(username)
                .signWith(key, SignatureAlgorithm.HS384)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 7))
                .compact();
    }

    public static Boolean isTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public static String extractUserName(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
