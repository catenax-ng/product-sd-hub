package net.catenax.selfdescriptionfactory;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import net.catenax.selfdescriptionfactory.dto.SDDocumentDto;
import net.catenax.selfdescriptionfactory.repo.DBVCRepository;
import net.catenax.selfdescriptionfactory.service.SDFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureEmbeddedDatabase(
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
        provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY
)
public class DatabaseTest {
    @Autowired
    DBVCRepository vcRepo;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SDFactory sdFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${app.verifiableCredentials.holder}")
    String holder;
    @Value("${app.verifiableCredentials.issuer}")
    String issuer;

    @Test
    @WithMockUser(username = "test", roles = "add_self_descriptions")
    public void testDB() throws Exception {
        var sdDTO = SDDocumentDto.builder()
                .issuer(issuer)
                .holder(holder)
                .company_number("DE-123")
                .headquarter_country("DE")
                .legal_country("DE")
                .bpn("BPN123")
                .build();

        var vcResp = mockMvc.perform(post("/selfdescription")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sdDTO))).andExpect(status().isCreated()).andReturn().getResponse();
        var respStr = vcResp.getContentAsString();
        Assert.assertNotNull(respStr);
        var resVC = VerifiableCredential.fromJson(respStr);

        var vcModel = vcRepo.findAll().get(0);
        var dbVc = VerifiableCredential.fromJson(objectMapper.writeValueAsString(vcModel.getVc()));
        Assert.assertEquals(resVC.getCredentialSubject(), dbVc.getCredentialSubject());
    }
}
