package com.technicaleval.transfers.exception;

public class SoapClientException extends RuntimeException {

    public SoapClientException(String message, Throwable cause) {
        super(message, cause);
    }
}