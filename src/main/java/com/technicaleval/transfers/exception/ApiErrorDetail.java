package com.technicaleval.transfers.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorDetail(
        @JsonProperty("ErrorCode") String errorCode,
        @JsonProperty("Message") String message,
        @JsonProperty("Path") String path
) {
}