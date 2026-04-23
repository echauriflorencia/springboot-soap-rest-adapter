package com.technicaleval.transfers.controller;

import com.technicaleval.transfers.dto.api.RecipientsGetResponse;
import com.technicaleval.transfers.service.RecipientsService;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/transfers")
public class RecipientsController {

    private final RecipientsService recipientsService;

    public RecipientsController(RecipientsService recipientsService) {
        this.recipientsService = recipientsService;
    }

    @GetMapping("/customers-document/{customer-document-number}/recipients")
    public RecipientsGetResponse getRecipients(
            @PathVariable("customer-document-number")
            @Pattern(regexp = "^\\d+$") String customerDocumentNumber,
            @RequestParam("customer-document-type")
            @Pattern(regexp = "^(01|02|03|11|101|125)$") String customerDocumentType
    ) {
        return recipientsService.getRecipients(customerDocumentNumber, customerDocumentType);
    }
}