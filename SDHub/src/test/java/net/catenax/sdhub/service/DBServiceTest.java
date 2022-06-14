package net.catenax.sdhub.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import net.catenax.sdhub.repo.CredentialSubject;
import net.catenax.sdhub.repo.DBVCEntity;
import net.catenax.sdhub.repo.DBVCRepository;
import net.catenax.sdhub.repo.VCModel;
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

    private static final DBVCEntity entity = new DBVCEntity(UUID.randomUUID(),
            new VCModel(
                    List.of("https://www.w3.org/2018/credentials/v1"),
                    List.of("VerifiableCredential"),
                    "https://catalog.demo.supplytree.org/api/user/catenax",
                    new Date(),
                    new CredentialSubject(
                            UUID.randomUUID(),
                            "did:web:www.aa.com",
                            "DE",
                            "DE",
                            "",
                            "",
                            "BPNAAAAAA"
                    )
            ));

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
        Assertions.assertNotNull(service.getVc(entity.getVc().getCredentialSubject().getId().toString()));
    }

//    @Test
//    public void getSelfDescriptionsTest() {
//        var vp = service.getSelfDescriptions(List.of(model.getId()));
//        Assertions.assertNotNull(vp);
//    }

}
