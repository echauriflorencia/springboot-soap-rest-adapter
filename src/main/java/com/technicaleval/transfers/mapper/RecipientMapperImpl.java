package com.technicaleval.transfers.mapper;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;
import com.technicaleval.transfers.client.soap.wsdl.PropiedadDTO;
import com.technicaleval.transfers.dto.api.AccountData;
import com.technicaleval.transfers.dto.api.RecipientData;
import com.technicaleval.transfers.dto.api.RecipientsGetResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RecipientMapperImpl implements RecipientMapper {

    @Override
    public RecipientsGetResponse toApiResponse(List<AgendaCBUDTO> agendaCbu) {
        if (agendaCbu == null || agendaCbu.isEmpty()) {
            return new RecipientsGetResponse(Collections.emptyList());
        }

        List<RecipientData> recipients = agendaCbu.stream()
                .map(this::toRecipientData)
                .toList();

        return new RecipientsGetResponse(recipients);
    }

    private RecipientData toRecipientData(AgendaCBUDTO agenda) {
        PropiedadDTO propiedad = agenda.getPropiedadDTO();
        AccountData account = new AccountData(
                normalizeText(agenda.getNroCBU()),
                toIntegerCode(propiedad != null ? propiedad.getCodigo() : null),
                normalizeText(propiedad != null ? propiedad.getDescripcion() : null),
                propiedad != null ? propiedad.isCtaCorriente() : null,
                propiedad != null ? propiedad.isPropia() : null
        );

        return new RecipientData(
                normalizeText(agenda.getCuitCuil()),
                normalizeText(agenda.getDescripcion()),
                account
        );
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        return value.strip();
    }

    private Integer toIntegerCode(String code) {
        if (code == null) {
            return null;
        }

        String normalizedCode = code.strip();
        if (normalizedCode.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(normalizedCode);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}