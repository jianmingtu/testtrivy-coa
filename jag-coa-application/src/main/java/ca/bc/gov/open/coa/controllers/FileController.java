package ca.bc.gov.open.coa.controllers;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.configuration.SoapConfig;
import ca.bc.gov.open.coa.exceptions.ORDSException;
import ca.bc.gov.open.coa.models.OrdsErrorLog;
import ca.bc.gov.open.coa.models.RequestSuccessLog;
import ca.bc.gov.open.coa.one.GetFileMimeRequest;
import ca.bc.gov.open.coa.one.GetFileMimeResponse;
import ca.bc.gov.open.coa.one.GetFileSizeRequest;
import ca.bc.gov.open.coa.one.GetFileSizeResponse;
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
public class FileController {
    @Value("${coa.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CoaConfig coaConfig;

    @Autowired
    public FileController(
            RestTemplate restTemplate, ObjectMapper objectMapper, CoaConfig coaConfig) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.coaConfig = coaConfig;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getFileMimeRequest")
    @ResponsePayload
    public GetFileMimeResponse getFileMime(@RequestPayload GetFileMimeRequest search)
            throws JsonProcessingException {
        addEndpointHeader("GetFileMimeRequest");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "file/mime")
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
            HttpEntity<GetFileMimeResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetFileMimeResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "getFileMimeRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getFileMimeRequest",
                                    ex.getMessage(),
                                    search)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getFileSizeRequest")
    @ResponsePayload
    public GetFileSizeResponse getFileSize(@RequestPayload GetFileSizeRequest search)
            throws JsonProcessingException {
        addEndpointHeader("GetFileSizeRequest");

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "file/size")
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
            HttpEntity<GetFileSizeResponse> resp =
                    restTemplate.exchange(
                            builder.build(true).toUri(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            GetFileSizeResponse.class);

            log.info(
                    objectMapper.writeValueAsString(
                            new RequestSuccessLog("Request Success", "GetFileSizeRequest")));
            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "GetFileSizeRequest",
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
