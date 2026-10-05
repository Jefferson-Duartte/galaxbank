package com.jefferson.galaxbank.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
