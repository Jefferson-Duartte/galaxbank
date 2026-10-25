package com.jefferson.galaxbank.account.service;

import com.jefferson.galaxbank.account.entity.Account;
import com.jefferson.galaxbank.account.repository.AccountRepository;
import com.jefferson.galaxbank.auth.entity.User;
import com.jefferson.galaxbank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final int ACCOUNT_NUMBER_LENGTH = 10;
    private static final long ACCOUNT_NUMBER_BOUND = 10000000000L;
    private static final int MAX_NUMBER_GENERATION_ATTEMPS = 5;
    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public Account createAccount(User user) {

        Account newAccount = Account.builder()
                .user(user)
                .number(generateAccountNumber())
                .build();

        return accountRepository.save(newAccount);

    }

    @Transactional(readOnly = true)
    public Account findByUserId(UUID userId) {
        return accountRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(
                "Conta não encontrada!",
                HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(UUID userId) {
        return findByUserId(userId).getBalance();
    }

    private String generateAccountNumber() {
        for (int attempt = 0; attempt < MAX_NUMBER_GENERATION_ATTEMPS; attempt++) {
            long value = secureRandom.nextLong(ACCOUNT_NUMBER_BOUND);
            String number = String.format("%0" + ACCOUNT_NUMBER_LENGTH + "d", value);

            if (!accountRepository.existsByNumber(number)) {
                return number;
            }
        }

        throw new IllegalStateException("Não foi possível gerar um número de conta único");
    }


}
