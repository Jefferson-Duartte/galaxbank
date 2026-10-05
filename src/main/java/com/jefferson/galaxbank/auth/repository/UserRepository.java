package com.jefferson.galaxbank.auth.repository;

import com.jefferson.galaxbank.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
