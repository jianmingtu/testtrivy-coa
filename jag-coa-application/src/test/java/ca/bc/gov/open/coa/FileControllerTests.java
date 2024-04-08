package ca.bc.gov.open.coa;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.controllers.FileController;
import ca.bc.gov.open.coa.one.GetFileMimeRequest;
import ca.bc.gov.open.coa.one.GetFileMimeResponse;
import ca.bc.gov.open.coa.one.GetFileSizeRequest;
import ca.bc.gov.open.coa.one.GetFileSizeResponse;
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
public class FileControllerTests {
    @Mock private ObjectMapper objectMapper;
    @Mock private CoaConfig coaConfig;
    @Mock private RestTemplate restTemplate;
    @Mock private FileController fileController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileController = Mockito.spy(new FileController(restTemplate, objectMapper, coaConfig));
    }

    @Test
    public void getFileMimeTest() throws JsonProcessingException {
        var req = new GetFileMimeRequest();
        req.setDocumentGUID("A");
        var resp = new GetFileMimeResponse();

        resp.setMime("A");
        ResponseEntity<GetFileMimeResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetFileMimeResponse>>any()))
                .thenReturn(responseEntity);

        var out = fileController.getFileMime(req);
        Assertions.assertNotNull(out);
    }

    @Test
    public void getFileSizeTest() throws JsonProcessingException {
        var req = new GetFileSizeRequest();
        req.setDocumentGUID("A");
        var resp = new GetFileSizeResponse();

        resp.setSize(Long.MAX_VALUE);
        ResponseEntity<GetFileSizeResponse> responseEntity =
                new ResponseEntity<>(resp, HttpStatus.OK);

        // Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(URI.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetFileSizeResponse>>any()))
                .thenReturn(responseEntity);

        var out = fileController.getFileSize(req);
        Assertions.assertNotNull(out);
    }
}
