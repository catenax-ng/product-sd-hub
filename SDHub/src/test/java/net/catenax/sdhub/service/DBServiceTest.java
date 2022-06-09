package net.catenax.sdhub.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import net.catenax.sdhub.repo.VCModel;
import net.catenax.sdhub.repo.VerifiableCredentialRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
)
public class DBServiceTest {

    private static final VCModel model = new VCModel("b034c9ab-c099-4a55-a0a9-26113c29dc2f", "did:web:www.aa.com", "DE", "DE", "", "", "BPNAAAAAA",
            "{\"@context\": [\"https://www.w3.org/2018/credentials/v1\"],\"id\": \"92786175-9dbd-4bbf-9790-e876294e8023\",\"type\": [\"VerifiablePresentation\"],\"holder\": \"did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh\",\"verifiableCredential\": [{\"id\": \"http://localhost:8080/selfdescription/vc/b034c9ab-c099-4a55-a0a9-26113c29dc2f\",\"@context\": [\"https://www.w3.org/2018/credentials/v1\",\"https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld\"],\"type\": [\"VerifiableCredential\",\"SD-document\"],\"issuer\": \"did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh\",\"issuanceDate\": \"2022-04-10T22:27:23Z\",\"expirationDate\": \"2022-07-09T22:27:23Z\",\"credentialSubject\": {\"bpn\": \"BPNAAAAAA\",\"company_number\": \"did:web:www.aa.com\",\"headquarter_country\": \"DE\",\"legal_country\": \"DE\",\"id\": \"did:indy:idunion:test:JFcJRR9NSmtZaQGFMJuEjh\"}}]}");

    @Autowired
    private DBService service;

    @Autowired
    private VerifiableCredentialRepo repo;

    @BeforeEach
    public void before() {
        repo.deleteAll();
        repo.save(model);
    }

    @Test
    public void getVcTest() {
        Assertions.assertNotNull(service.getVc(model.getId()));
    }

//    @Test
//    public void getSelfDescriptionsTest() {
//        var vp = service.getSelfDescriptions(List.of(model.getId()));
//        Assertions.assertNotNull(vp);
//    }

}
