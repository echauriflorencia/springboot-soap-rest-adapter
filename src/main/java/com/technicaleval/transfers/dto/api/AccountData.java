package com.technicaleval.transfers.dto.api;

public record AccountData(
        String cbu,
        Integer code,
        String description,
        Boolean current,
        Boolean own
) {
}
