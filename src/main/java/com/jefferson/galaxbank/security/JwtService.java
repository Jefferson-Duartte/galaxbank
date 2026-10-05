package com.jefferson.galaxbank.security;

import com.jefferson.galaxbank.auth.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.private-key}")
    private String privateKeyString;

    @Value("${jwt.public-key}")
    private String publicKeyString;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public String generateToken(String cpf) {
        return Jwts.builder()
                .subject(cpf)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getPrivateKey())
                .compact();
    }

    public String extractCpf(String token) {
        return extractClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return !tokenBlacklistRepository.existsByToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private PrivateKey getPrivateKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave privada RSA", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar chave publica RSA", e);
        }
    }

}
