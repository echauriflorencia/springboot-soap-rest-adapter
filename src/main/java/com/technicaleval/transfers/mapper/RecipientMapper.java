package com.technicaleval.transfers.mapper;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;
import com.technicaleval.transfers.dto.api.RecipientsGetResponse;

import java.util.List;

public interface RecipientMapper {

    RecipientsGetResponse toApiResponse(List<AgendaCBUDTO> agendaCbu);
}
