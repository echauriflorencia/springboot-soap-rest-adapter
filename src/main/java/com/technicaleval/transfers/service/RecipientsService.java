package com.technicaleval.transfers.service;

import com.technicaleval.transfers.dto.api.RecipientsGetResponse;

public interface RecipientsService {

    RecipientsGetResponse getRecipients(String customerDocumentNumber, String customerDocumentType);
}
