package com.jefferson.galaxbank.account.mapper;

import com.jefferson.galaxbank.account.dto.response.AccountResponse;
import com.jefferson.galaxbank.account.dto.response.BalanceResponse;
import com.jefferson.galaxbank.account.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountMapper {

    @Mapping(target = "userId", source = "user.id")
    AccountResponse toResponse(Account account);

    @Mapping(target = "currency", constant = "BRL")
    BalanceResponse toBalanceResponse(Account account);

}
