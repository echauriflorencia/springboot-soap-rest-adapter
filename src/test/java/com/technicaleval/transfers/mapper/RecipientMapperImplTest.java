package com.technicaleval.transfers.mapper;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;
import com.technicaleval.transfers.client.soap.wsdl.PropiedadDTO;
import com.technicaleval.transfers.dto.api.RecipientData;
import com.technicaleval.transfers.dto.api.RecipientsGetResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipientMapperImplTest {

    private final RecipientMapperImpl mapper = new RecipientMapperImpl();

    @Test
    void shouldMapSoapAgendaFieldsToApiResponse() {
        AgendaCBUDTO agenda = new AgendaCBUDTO();
        agenda.setCuitCuil(" 20123456789 ");
        agenda.setDescripcion(" Descripcion destinatario ");
        agenda.setNroCBU(" 1234567890123456789012 ");

        PropiedadDTO propiedad = new PropiedadDTO();
        propiedad.setCodigo(" 2 ");
        propiedad.setDescripcion(" Caja de ahorro ");
        propiedad.setCtaCorriente(Boolean.FALSE);
        propiedad.setPropia(Boolean.TRUE);
        agenda.setPropiedadDTO(propiedad);

        RecipientsGetResponse response = mapper.toApiResponse(List.of(agenda));

        assertNotNull(response);
        assertEquals(1, response.recipient().size());

        RecipientData recipient = response.recipient().get(0);
        assertEquals("20123456789", recipient.cuit());
        assertEquals("Descripcion destinatario", recipient.description());

        assertNotNull(recipient.account());
        assertEquals("1234567890123456789012", recipient.account().cbu());
        assertEquals(2, recipient.account().code());
        assertEquals("Caja de ahorro", recipient.account().description());
        assertEquals(Boolean.FALSE, recipient.account().current());
        assertEquals(Boolean.TRUE, recipient.account().own());
    }

    @Test
    void shouldTrimBlankSoapStringsToEmptyValues() {
        AgendaCBUDTO agenda = new AgendaCBUDTO();
        agenda.setCuitCuil("           ");
        agenda.setDescripcion("   ");
        agenda.setNroCBU(" 2850001040094075882478 ");

        PropiedadDTO propiedad = new PropiedadDTO();
        propiedad.setCodigo("3");
        propiedad.setDescripcion("   ");
        propiedad.setCtaCorriente(Boolean.FALSE);
        propiedad.setPropia(Boolean.TRUE);
        agenda.setPropiedadDTO(propiedad);

        RecipientsGetResponse response = mapper.toApiResponse(List.of(agenda));

        RecipientData recipient = response.recipient().get(0);
        assertEquals("", recipient.cuit());
        assertEquals("", recipient.description());
        assertEquals("2850001040094075882478", recipient.account().cbu());
        assertEquals("", recipient.account().description());
    }

    @Test
    void shouldReturnNullAccountCodeWhenSoapCodeIsNotNumeric() {
        AgendaCBUDTO agenda = new AgendaCBUDTO();
        agenda.setCuitCuil("27123456789");
        agenda.setDescripcion("Otro destinatario");
        agenda.setNroCBU("0000000000000000000000");

        PropiedadDTO propiedad = new PropiedadDTO();
        propiedad.setCodigo("NO_NUMERIC");
        agenda.setPropiedadDTO(propiedad);

        RecipientsGetResponse response = mapper.toApiResponse(List.of(agenda));

        assertNull(response.recipient().get(0).account().code());
    }

    @Test
    void shouldReturnEmptyListWhenSoapResponseIsNullOrEmpty() {
        RecipientsGetResponse nullResponse = mapper.toApiResponse(null);
        RecipientsGetResponse emptyResponse = mapper.toApiResponse(List.of());

        assertTrue(nullResponse.recipient().isEmpty());
        assertTrue(emptyResponse.recipient().isEmpty());
    }
}
