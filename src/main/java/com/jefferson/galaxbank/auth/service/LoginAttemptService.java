package com.jefferson.galaxbank.auth.service;

import com.jefferson.galaxbank.auth.entity.User;
import com.jefferson.galaxbank.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailedAttempt(User user) {
        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        System.out.println("Tentativas de login: " + attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLockedUntil(OffsetDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            user.setLoginAttempts(0);
            System.out.println("Conta bloqueada até: " + user.getLockedUntil());
        }

        userRepository.save(user);
    }
}