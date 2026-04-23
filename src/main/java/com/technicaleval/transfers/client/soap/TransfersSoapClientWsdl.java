package com.technicaleval.transfers.client.soap;

import com.technicaleval.transfers.client.soap.wsdl.AgendaCBUDTO;
import com.technicaleval.transfers.client.soap.wsdl.TransferenciasV2;
import com.technicaleval.transfers.client.soap.wsdl.TransferenciasV2Service;
import com.technicaleval.transfers.client.soap.wsdl.UsuarioDTO;
import com.technicaleval.transfers.config.SoapClientConfig;
import com.technicaleval.transfers.exception.SoapClientException;
import org.springframework.stereotype.Component;

import jakarta.xml.ws.BindingProvider;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TransfersSoapClientWsdl implements TransfersSoapClient {

    private static final String WSDL_RESOURCE_PATH = "wsdl/PRISMA_TransferenciasService.wsdl";
    private static final String CONNECT_TIMEOUT_PROPERTY = "com.sun.xml.ws.connect.timeout";
    private static final String READ_TIMEOUT_PROPERTY = "com.sun.xml.ws.request.timeout";

    private final SoapClientConfig properties;
    private final TransferenciasV2Service service;

    public TransfersSoapClientWsdl(SoapClientConfig properties) {
        this.properties = properties;
        this.service = new TransferenciasV2Service(resolveWsdlUrl());
    }

    @Override
    public List<AgendaCBUDTO> getAgendaCbu(String documentNumber, String documentType) {
        if (documentNumber == null || documentType == null) {
            return List.of();
        }

        try {
            TransferenciasV2 transferenciasPort = createPort();
            List<AgendaCBUDTO> response = transferenciasPort.getAgendaCBU(buildUsuario(documentNumber, documentType), null);
            if (response == null || response.isEmpty()) {
                return List.of();
            }

            List<AgendaCBUDTO> safeResponse = new ArrayList<>(response.size());
            for (AgendaCBUDTO recipient : response) {
                if (recipient != null) {
                    safeResponse.add(recipient);
                }
            }
            return safeResponse;
        } catch (RuntimeException ex) {
            throw new SoapClientException("No se pudo invocar el servicio SOAP de transferencias", ex);
        }
    }

    private TransferenciasV2 createPort() {
        TransferenciasV2 port = service.getTransferenciasV2Port();
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();

        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, requireConfig(properties.endpoint(), "soap.transfers.endpoint"));
        requestContext.put(CONNECT_TIMEOUT_PROPERTY, requirePositive(properties.connectTimeoutMs(), "soap.transfers.connect-timeout-ms"));
        requestContext.put(READ_TIMEOUT_PROPERTY, requirePositive(properties.readTimeoutMs(), "soap.transfers.read-timeout-ms"));

        return port;
    }

    private UsuarioDTO buildUsuario(String documentNumber, String documentType) {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNroDocumento(documentNumber);
        usuario.setTipoDocumento(documentType);
        usuario.setPassword(requireConfig(properties.password(), "soap.transfers.password"));
        return usuario;
    }

    private URL resolveWsdlUrl() {
        URL wsdlUrl = getClass().getClassLoader().getResource(WSDL_RESOURCE_PATH);
        if (wsdlUrl == null) {
            throw new IllegalStateException("WSDL resource not found: " + WSDL_RESOURCE_PATH);
        }
        return wsdlUrl;
    }

    private String requireConfig(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + propertyName);
        }
        return value;
    }

    private Integer requirePositive(Integer value, String propertyName) {
        if (value == null || value <= 0) {
            throw new IllegalStateException("Property must be greater than zero: " + propertyName);
        }
        return value;
    }
}

