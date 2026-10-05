package com.jefferson.galaxbank.auth.service;

import com.jefferson.galaxbank.auth.dto.request.LoginRequest;
import com.jefferson.galaxbank.auth.dto.request.RegisterRequest;
import com.jefferson.galaxbank.auth.dto.response.LoginResponse;
import com.jefferson.galaxbank.auth.entity.TokenBlacklist;
import com.jefferson.galaxbank.auth.entity.User;
import com.jefferson.galaxbank.auth.repository.TokenBlacklistRepository;
import com.jefferson.galaxbank.auth.repository.UserRepository;
import com.jefferson.galaxbank.security.JwtService;
import com.jefferson.galaxbank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado", HttpStatus.CONFLICT);
        }

        if (request.getPassword().equals(request.getTransactionPassword())) {
            throw new BusinessException(
                    "Senha de transação não pode ser igual à senha de acesso",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        User user = User.builder()
                .name(request.getName())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .transactionPassword(passwordEncoder.encode(request.getTransactionPassword()))
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByCpf(request.getCpf())
                .orElseThrow(() -> new BusinessException(
                        "CPF ou senha inválidos",
                        HttpStatus.UNAUTHORIZED
                ));

        if (!user.isAccountNonLocked()) {
            throw new BusinessException(
                    "Conta bloqueada. Tente novamente mais tarde",
                    HttpStatus.FORBIDDEN
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.handleFailedAttempt(user);
            throw new BusinessException("CPF ou senha inválidos", HttpStatus.UNAUTHORIZED);
        }

        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getCpf());
        return LoginResponse.of(token);
    }

    @Transactional
    public void logout(String token) {
        TokenBlacklist blacklisted = TokenBlacklist.builder()
                .token(token)
                .expiresAt(jwtService.extractExpiration(token)
                        .toInstant()
                        .atOffset(java.time.ZoneOffset.UTC))
                .build();

        tokenBlacklistRepository.save(blacklisted);
    }
}





