package ca.bc.gov.open.coa;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.controllers.TicketController;
import ca.bc.gov.open.coa.one.GetTicketRequest;
import ca.bc.gov.open.coa.one.GetTicketResponse;
import ca.bc.gov.open.coa.one.GetTicketedUrlRequest;
import ca.bc.gov.open.coa.one.GetTicketedUrlResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TicketControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private CoaConfig coaConfig;
    @Mock private RestTemplate restTemplate;
    @Mock private TicketController ticketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketController = Mockito.spy(new TicketController(restTemplate, objectMapper, coaConfig));
    }

    @Test
    public void getTicketedUrlTest() throws JsonProcessingException {
        var req = new GetTicketedUrlRequest();
        req.setDocumentGUID("A");
        var resp = new GetTicketedUrlResponse();

        resp.setTicketedUrl("A");
        ResponseEntity<GetTicketedUrlResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetTicketedUrlResponse>>any()))
                .thenReturn(responseEntity);

        var out = ticketController.getTicketedUrl(req);
        Assertions.assertNotNull(out);
    }

    @Test
    public void getTicketTest() throws JsonProcessingException {
        var req = new GetTicketRequest();
        req.setDocumentGUID("A");
        var resp = new GetTicketResponse();

        resp.setTicket("A");
        ResponseEntity<GetTicketResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetTicketResponse>>any()))
                .thenReturn(responseEntity);

        var out = ticketController.getTicket(req);
        Assertions.assertNotNull(out);
    }
}
