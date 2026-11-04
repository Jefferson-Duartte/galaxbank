package com.jefferson.galaxbank.account.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(UUID id, UUID userId, String number, BigDecimal balance, boolean active,
                              OffsetDateTime createdAt, OffsetDateTime updatedAt) {
}
