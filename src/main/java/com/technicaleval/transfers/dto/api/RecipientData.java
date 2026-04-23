package com.technicaleval.transfers.dto.api;

public record RecipientData(
        String cuit,
        String description,
        AccountData account
) {
}
