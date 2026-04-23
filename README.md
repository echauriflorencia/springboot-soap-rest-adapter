# Evaluación técnica - Spring Boot

Aplicacion Spring Boot que implementa el endpoint REST de consulta de destinatarios de transferencias, consumiendo un backend SOAP definido por WSDL.

## Objetivo de la entrega

Implementar el caso exitoso (`HTTP 200`) del endpoint definido en `docs/app-transfers_v1.3.yaml`, mapeando la respuesta SOAP al esquema REST requerido.

## Requisitos previos

- Java 17+
- Node.js 18+ (para ejecutar el mock SOAP local)
- Maven (opcional, se incluye wrapper: `./mvnw`)

## Estructura relevante

- `src/main/java/com/technicaleval/transfers/controller`: capa REST
- `src/main/java/com/technicaleval/transfers/service`: logica de aplicacion
- `src/main/java/com/technicaleval/transfers/client/soap`: cliente SOAP
- `src/main/java/com/technicaleval/transfers/mapper`: mapeo SOAP -> API
- `src/main/resources/application.yml`: configuracion
- `src/main/resources/wsdl/PRISMA_TransferenciasService.wsdl`: contrato SOAP
- `mock-server.js`: mock SOAP local

## Configuracion por ambiente

Propiedades bajo `soap.transfers`:

- `SOAP_TRANSFERS_ENDPOINT` (default: `http://localhost:3003/servicios/transferenciasV2`)
- `SOAP_TRANSFERS_PASSWORD` (default: `changeme`)
- `SOAP_TRANSFERS_CONNECT_TIMEOUT_MS` (default: `5000`)
- `SOAP_TRANSFERS_READ_TIMEOUT_MS` (default: `8000`)

Ejemplo:

```bash
export SOAP_TRANSFERS_ENDPOINT=http://localhost:3003/servicios/transferenciasV2
export SOAP_TRANSFERS_PASSWORD=changeme
export SOAP_TRANSFERS_CONNECT_TIMEOUT_MS=5000
export SOAP_TRANSFERS_READ_TIMEOUT_MS=8000
```

## Ejecucion local

1. Levantar el mock SOAP:

```bash
node mock-server.js
```

2. En otra terminal, levantar la API:

```bash
./mvnw spring-boot:run
```

3. Probar el endpoint:

```bash
curl --request GET \
  'http://localhost:8080/v1/transfers/customers-document/32345379/recipients?customer-document-type=01'
```

Tambien puede utilizarse Mockoon importando `docs/Transfers-Mock-get-recipients.json` en puerto `3003`.

## Endpoint implementado

- Metodo: `GET`
- Path: `/v1/transfers/customers-document/{customer-document-number}/recipients`
- Query param obligatorio: `customer-document-type`
- Valores permitidos de `customer-document-type`: `01`, `02`, `03`, `11`, `101`, `125`
- Restriccion de `customer-document-number`: solo digitos

## Ejemplo de respuesta 200

```json
{
  "recipient": [
    {
      "cuit": "27283221145",
      "description": "ROSA LOPEZ",
      "account": {
        "cbu": "2850672840027388702207",
        "code": 4,
        "description": "Es Otra Cuenta No Propia",
        "current": true,
        "own": false
      }
    }
  ]
}
```

## Cobertura de requerimientos de la consigna

- API REST implementada segun contrato entregado para el caso exitoso.
- Integracion con backend SOAP mediante cliente JAX-WS generado desde WSDL.
- Mapeo de datos SOAP a esquema REST encapsulado en `RecipientMapper`.
- Flujo de prueba local habilitado con mock SOAP (`mock-server.js` o Mockoon).
- Proyecto documentado para ejecucion y validacion funcional.

## Decisiones tecnicas

- Arquitectura por capas: `controller -> service -> client.soap -> mapper`.
- Cliente SOAP generado con `jaxws-maven-plugin` en `target/generated-sources/wsimport`.
- Agregado de generated sources al build mediante `build-helper-maven-plugin`.
- Validaciones de entrada en controller con Bean Validation (`@Pattern`).
- Normalizacion de strings SOAP (trim) y conversion segura de codigo de cuenta a `Integer`.

## Pruebas

- Ejecutar:

```bash
./mvnw test
```

