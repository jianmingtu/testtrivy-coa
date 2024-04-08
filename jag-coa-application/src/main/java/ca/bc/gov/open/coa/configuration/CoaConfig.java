package ca.bc.gov.open.coa.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CoaConfig {
    @Value("${coa.object-store-app-id}")
    private String coaAppId;

    @Value("${coa.object-store-psw}")
    private String coaPassword;

    @Value("${coa.object-store-username}")
    private String coaUsername;

    @Value("${coa.object-store-version}")
    private String coaVersion;

    @Value("${coa.object-store-db-id}")
    private String coaDatabaseId;

    @Value("${coa.object-store-ticket-lifetime}")
    private String coaTicketLifeTime;

    public CoaConfig() {}
}
