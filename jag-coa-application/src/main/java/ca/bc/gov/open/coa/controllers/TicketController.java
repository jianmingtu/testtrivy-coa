package ca.bc.gov.open.coa.controllers;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.configuration.SoapConfig;
import ca.bc.gov.open.coa.exceptions.ORDSException;
import ca.bc.gov.open.coa.models.OrdsErrorLog;
import ca.bc.gov.open.coa.models.RequestSuccessLog;
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
public class TicketController {

    @Value("${coa.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CoaConfig coaConfig;

    @Autowired
    public TicketController(
            RestTemplate restTemplate, ObjectMapper objectMapper, CoaConfig coaConfig) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.coaConfig = coaConfig;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getTicketedUrlRequest")
    @ResponsePayload
    public GetTicketedUrlResponse getTicketedUrl(@RequestPayload GetTicketedUrlRequest search)
            throws JsonProcessingException {
        addEndpointHeader("GetTicketedUrlRequest");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "ticket/url")
                        .queryParam(
                                "documentGUID",
                                URLEncoder.encode(search.getDocumentGUID(), StandardCharsets.UTF_8))
                        .queryParam("appId", coaConfig.getCoaAppId())
                        .queryParam("password", coaConfig.getCoaPassword())
                        .queryParam("userName", coaConfig.getCoaUsername())
                        .queryParam("databaseId", coaConfig.getCoaDatabaseId())
                        .queryParam(
                                "ticketLifetime",
                                search.getTicketLifeTime() == null
                                        ? coaConfig.getCoaTicketLifeTime()
                                        : search.getTicketLifeTime());

        try {
            HttpEntity<GetTicketedUrlResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetTicketedUrlResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "GetTicketedUrlRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "GetTicketedUrlRequest",
                                    ex.getMessage(),
                                    search)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getTicketRequest")
    @ResponsePayload
    public GetTicketResponse getTicket(@RequestPayload GetTicketRequest search)
            throws JsonProcessingException {
        addEndpointHeader("GetTicketRequest");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "ticket")
                        .queryParam(
                                "documentGUID",
                                URLEncoder.encode(search.getDocumentGUID(), StandardCharsets.UTF_8))
                        .queryParam("appId", coaConfig.getCoaAppId())
                        .queryParam("password", coaConfig.getCoaPassword())
                        .queryParam("userName", coaConfig.getCoaUsername())
                        .queryParam("databaseId", coaConfig.getCoaDatabaseId())
                        .queryParam(
                                "ticketLifetime",
                                search.getTicketLifeTime() == null
                                        ? coaConfig.getCoaTicketLifeTime()
                                        : search.getTicketLifeTime());

        try {
            HttpEntity<GetTicketResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetTicketResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "GetTicketRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "GetTicketRequest",
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
