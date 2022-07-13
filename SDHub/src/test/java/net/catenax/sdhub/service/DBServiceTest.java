package net.catenax.sdhub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import net.catenax.sdhub.repo.DBVCEntity;
import net.catenax.sdhub.repo.DBVCRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
)
public class DBServiceTest {
    private static final DBVCEntity entity;
    private static final String credentialSubjectId;

    static {
        var objectMapper = new ObjectMapper();
        credentialSubjectId = UUID.randomUUID().toString();
        var credentialSubject = objectMapper.createObjectNode()
                .put("id", credentialSubjectId)
                .put("company_number", "did:web:www.aa.com")
                .put("headquarter_country", "DE")
                .put("legal_country", "DE")
                .put("service_provider", "")
                .put("sd_type", "")
                .put("bpn", "BPNAAAAAA");

        var contexts = objectMapper.createArrayNode();
        List.of("https://www.w3.org/2018/credentials/v1").forEach(contexts::add);

        var types = objectMapper.createArrayNode();
        List.of("VerifiableCredential").forEach(types::add);

        var root = objectMapper.createObjectNode()
                .put("issuer", "https://catalog.demo.supplytree.org/api/user/catenax")
                .put("issuanceDate", new Date().toInstant().toString());

        root = root.set("@context", contexts);
        root = root.set("type", types);
        root = root.set("credentialSubject", credentialSubject);

        entity = new DBVCEntity(UUID.randomUUID(), root.toString());
    }

    @Autowired
    private DBService service;

    @Autowired
    private DBVCRepository repo;

    @BeforeEach
    public void before() {
        repo.deleteAll();
        repo.save(entity);
    }

    @Test
    public void getVcTest() {
        Assertions.assertNotNull(service.getVc(credentialSubjectId));
    }

//    @Test
//    public void getSelfDescriptionsTest() {
//        var vp = service.getSelfDescriptions(List.of(model.getId()));
//        Assertions.assertNotNull(vp);
//    }

}
