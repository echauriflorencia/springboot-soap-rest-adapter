package com.technicaleval.transfers.client.soap;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;

import java.util.List;

public interface TransfersSoapClient {

    List<AgendaCBUDTO> getAgendaCbu(String documentNumber, String documentType);
}