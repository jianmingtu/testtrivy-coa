package ca.bc.gov.open.coa.models;

import ca.bc.gov.open.coa.configuration.CoaConfig;
import ca.bc.gov.open.coa.one.StoreDocumentAsyncRequest;
import ca.bc.gov.open.coa.one.StoreDocumentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StoreDocumentBody extends StoreDocumentRequest {
    private String appId;
    private String userName;
    private String password;
    private String databaseId;
    private String ticketLifetime;

    private void setCoaConfig(CoaConfig coaConfig) {
        appId = coaConfig.getCoaAppId();
        userName = coaConfig.getCoaUsername();
        password = coaConfig.getCoaPassword();
        databaseId = coaConfig.getCoaDatabaseId();
        ticketLifetime = coaConfig.getCoaTicketLifeTime();
    }

    public StoreDocumentBody(StoreDocumentRequest storeDocumentRequest, CoaConfig coaConfig) {
        base64Document = storeDocumentRequest.getBase64Document();
        filename = storeDocumentRequest.getFilename();
        applicationViewGrant = storeDocumentRequest.getApplicationViewGrant();
        setCoaConfig(coaConfig);
    }

    public StoreDocumentBody(StoreDocumentAsyncRequest storeDocumentRequest, CoaConfig coaConfig) {
        filename = storeDocumentRequest.getFilename();
        applicationViewGrant = storeDocumentRequest.getApplicationViewGrant();
        setCoaConfig(coaConfig);
    }
}
