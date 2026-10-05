package com.jefferson.galaxbank.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type;

    public static LoginResponse of(String token) {
        return new LoginResponse(token, "Bearer");
    }
}
