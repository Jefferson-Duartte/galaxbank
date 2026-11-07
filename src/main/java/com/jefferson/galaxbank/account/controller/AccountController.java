package com.jefferson.galaxbank.account.controller;

import com.jefferson.galaxbank.account.dto.response.AccountResponse;
import com.jefferson.galaxbank.account.dto.response.BalanceResponse;
import com.jefferson.galaxbank.account.service.AccountService;
import com.jefferson.galaxbank.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping(path = "/me")
        public ResponseEntity<AccountResponse> getMyAccount(@AuthenticationPrincipal User user) {
        AccountResponse response = accountService.getByUserId(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/me/balance")
    public ResponseEntity<BalanceResponse> getMyBalance(@AuthenticationPrincipal User user){
        BalanceResponse response = accountService.getBalance(user.getId());
        return ResponseEntity.ok(response);
    }

}
