package ca.bc.gov.open.coa.controllers;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.configuration.SoapConfig;
import ca.bc.gov.open.coa.exceptions.ORDSException;
import ca.bc.gov.open.coa.models.OrdsErrorLog;
import ca.bc.gov.open.coa.models.RequestSuccessLog;
import ca.bc.gov.open.coa.models.StoreDocumentBody;
import ca.bc.gov.open.coa.one.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

@Slf4j
@Endpoint
public class StorageController {

    @Value("${coa.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CoaConfig coaConfig;

    @Autowired
    public StorageController(
            RestTemplate restTemplate, ObjectMapper objectMapper, CoaConfig coaConfig) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.coaConfig = coaConfig;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getDocumentUploadStateRequest")
    @ResponsePayload
    public GetDocumentUploadStateResponse getDocumentUploadState(
            @RequestPayload GetDocumentUploadStateRequest search) throws JsonProcessingException {
        addEndpointHeader("GetDocumentUploadStateRequest");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "doc/state")
                        .queryParam(
                                "documentGUID",
                                URLEncoder.encode(search.getDocumentGUID(), StandardCharsets.UTF_8))
                        .queryParam("appId", coaConfig.getCoaAppId())
                        .queryParam("password", coaConfig.getCoaPassword())
                        .queryParam("userName", coaConfig.getCoaUsername())
                        .queryParam("version", coaConfig.getCoaVersion())
                        .queryParam("databaseId", coaConfig.getCoaDatabaseId())
                        .queryParam("ticketLifetime", coaConfig.getCoaTicketLifeTime());

        try {
            HttpEntity<GetDocumentUploadStateResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetDocumentUploadStateResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog(
                                    "Request Success", "getDocumentUploadStateRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "GetDocumentUploadStateRequest",
                                    ex.getMessage(),
                                    search)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "storeDocumentAsyncRequest")
    @ResponsePayload
    public StoreDocumentAsyncResponse storeDocumentAsync(
            @RequestPayload StoreDocumentAsyncRequest search) throws JsonProcessingException {
        addEndpointHeader("StoreDocumentAsyncRequest");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "doc/async");
        HttpEntity<StoreDocumentBody> payload =
                new HttpEntity<>(new StoreDocumentBody(search, coaConfig), new HttpHeaders());

        log.info("request + " + objectMapper.writeValueAsString(payload.getBody()));
        try {
            HttpEntity<StoreDocumentAsyncResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.POST,
                            payload,
                            StoreDocumentAsyncResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "StoreDocumentAsyncRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "StoreDocumentAsyncRequest",
                                    ex.getMessage(),
                                    search)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "storeDocumentRequest")
    @ResponsePayload
    public StoreDocumentResponse storeDocument(@RequestPayload StoreDocumentRequest search)
            throws JsonProcessingException {
        addEndpointHeader("StoreDocumentResponse");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(host + "doc");
        HttpEntity<StoreDocumentBody> payload =
                new HttpEntity<>(new StoreDocumentBody(search, coaConfig), new HttpHeaders());

        try {
            HttpEntity<StoreDocumentResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.POST,
                            payload,
                            StoreDocumentResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "StoreDocumentRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "StoreDocumentRequest",
                                    ex.getMessage(),
                                    search)));
            throw new ORDSException();
        }
    }

    private void addEndpointHeader(String endpoint) {
        try {
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpServletConnection connection = (HttpServletConnection) context.getConnection();
            connection.addResponseHeader("Endpoint", endpoint);
        } catch (Exception ex) {
            log.warn("Failed to add endpoint response header");
        }
    }
}
