package ca.bc.gov.open.coa;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.coa.controllers.HealthController;
import ca.bc.gov.open.coa.one.GetHealth;
import ca.bc.gov.open.coa.one.GetHealthResponse;
import ca.bc.gov.open.coa.one.GetPing;
import ca.bc.gov.open.coa.one.GetPingResponse;
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
public class HealthControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private RestTemplate restTemplate;
    @Mock private HealthController healthController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        healthController = Mockito.spy(new HealthController(restTemplate, objectMapper));
    }

    @Test
    public void getPingTest() throws JsonProcessingException {
        var req = new GetPing();
        var resp = new GetPingResponse();

        resp.setStatus("A");
        ResponseEntity<GetPingResponse> responseEntity = new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetPingResponse>>any()))
                .thenReturn(responseEntity);

        var out = healthController.getPing(req);
        Assertions.assertNotNull(out);
    }

    @Test
    public void getHealthTest() throws JsonProcessingException {
        var req = new GetHealth();
        var resp = new GetHealthResponse();

        resp.setAppid("A");
        resp.setMethod("A");
        resp.setStatus("A");
        resp.setHost("A");
        resp.setInstance("A");
        resp.setVersion("A");
        resp.setCompatibility("A");
        ResponseEntity<GetHealthResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetHealthResponse>>any()))
                .thenReturn(responseEntity);

        var out = healthController.getHealth(req);
        Assertions.assertNotNull(out);
    }
}
