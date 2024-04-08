package ca.bc.gov.open.coa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.controllers.FileController;
import ca.bc.gov.open.coa.controllers.HealthController;
import ca.bc.gov.open.coa.controllers.StorageController;
import ca.bc.gov.open.coa.controllers.TicketController;
import ca.bc.gov.open.coa.exceptions.ORDSException;
import ca.bc.gov.open.coa.one.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdsErrorTests {
    @Autowired MockMvc mockMvc;

    @Mock private ObjectMapper objectMapper;
    @Mock private CoaConfig coaConfig;
    @Mock private RestTemplate restTemplate;

    @Mock private FileController fileController;
    @Mock private HealthController healthController;
    @Mock private StorageController storageController;
    @Mock private TicketController ticketController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileController = Mockito.spy(new FileController(restTemplate, objectMapper, coaConfig));
        healthController = Mockito.spy(new HealthController(restTemplate, objectMapper));
        storageController =
                Mockito.spy(new StorageController(restTemplate, objectMapper, coaConfig));
        ticketController = Mockito.spy(new TicketController(restTemplate, objectMapper, coaConfig));
    }

    @Test
    public void getHealthOrdsFailTest() {
        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHealth(new GetHealth()));
    }

    @Test
    public void getPingOrdsFailTest() {
        Assertions.assertThrows(ORDSException.class, () -> healthController.getPing(new GetPing()));
    }

    @Test
    public void getFileSizeOrdsFailTest() {
        GetFileSizeRequest req = new GetFileSizeRequest();
        req.setDocumentGUID("A");
        Assertions.assertThrows(ORDSException.class, () -> fileController.getFileSize(req));
    }

    @Test
    public void getFileMimeOrdsFailTest() {
        GetFileMimeRequest req = new GetFileMimeRequest();
        req.setDocumentGUID("A");
        Assertions.assertThrows(ORDSException.class, () -> fileController.getFileMime(req));
    }

    @Test
    public void getTicketedUrlOrdsFailTest() {
        GetTicketedUrlRequest req = new GetTicketedUrlRequest();
        req.setDocumentGUID("A");
        Assertions.assertThrows(ORDSException.class, () -> ticketController.getTicketedUrl(req));
    }

    @Test
    public void getTicketOrdsFailTest() {
        GetTicketRequest req = new GetTicketRequest();
        req.setDocumentGUID("A");
        Assertions.assertThrows(ORDSException.class, () -> ticketController.getTicket(req));
    }

    @Test
    public void getDocumentUploadStateOrdsFailTest() {
        GetDocumentUploadStateRequest req = new GetDocumentUploadStateRequest();
        req.setDocumentGUID("A");
        Assertions.assertThrows(
                ORDSException.class, () -> storageController.getDocumentUploadState(req));
    }

    @Test
    public void storeDocumentOrdsFailTest() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> storageController.storeDocument(new StoreDocumentRequest()));
    }

    @Test
    public void storeDocumentAsyncOrdsFailTest() {
        Assertions.assertThrows(
                ORDSException.class,
                () -> storageController.storeDocumentAsync(new StoreDocumentAsyncRequest()));
    }

    @Test
    public void securityTestFail_Then401() throws Exception {
        mockMvc.perform(post("/ws").contentType(MediaType.TEXT_XML))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }
}
