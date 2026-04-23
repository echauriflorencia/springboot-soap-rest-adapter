package com.technicaleval.transfers.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        @JsonProperty("Code") String code,
        @JsonProperty("Id") String id,
        @JsonProperty("Message") String message,
        @JsonProperty("Errors") List<ApiErrorDetail> errors
) {
}