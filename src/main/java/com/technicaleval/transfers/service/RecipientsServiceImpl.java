package com.technicaleval.transfers.service;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;
import com.technicaleval.transfers.client.soap.TransfersSoapClient;
import com.technicaleval.transfers.dto.api.RecipientsGetResponse;
import com.technicaleval.transfers.mapper.RecipientMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipientsServiceImpl implements RecipientsService {

    private final TransfersSoapClient transfersSoapClient;
    private final RecipientMapper recipientMapper;

    public RecipientsServiceImpl(TransfersSoapClient transfersSoapClient, RecipientMapper recipientMapper) {
        this.transfersSoapClient = transfersSoapClient;
        this.recipientMapper = recipientMapper;
    }

    @Override
    public RecipientsGetResponse getRecipients(String customerDocumentNumber, String customerDocumentType) {
        List<AgendaCBUDTO> soapResponse = transfersSoapClient.getAgendaCbu(customerDocumentNumber, customerDocumentType);
        return recipientMapper.toApiResponse(soapResponse);
    }
}
