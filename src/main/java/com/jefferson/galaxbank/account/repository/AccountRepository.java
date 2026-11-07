package com.jefferson.galaxbank.account.repository;

import com.jefferson.galaxbank.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByUserId(UUID userId);

    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);
}
