package com.jefferson.galaxbank.account.dto.response;

import com.jefferson.galaxbank.account.entity.Account;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal balance, String currency) {

    private final static String DEFAULT_CURRENCY = "BRL";

    public static BalanceResponse from(Account account) {
        return new BalanceResponse(account.getBalance(), DEFAULT_CURRENCY);
    }

}
