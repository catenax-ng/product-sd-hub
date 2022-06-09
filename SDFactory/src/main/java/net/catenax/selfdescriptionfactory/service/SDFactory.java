package net.catenax.selfdescriptionfactory.service;

import com.apicatalog.jsonld.document.JsonDocument;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import foundation.identity.jsonld.JsonLDUtils;
import lombok.RequiredArgsConstructor;
import net.catenax.selfdescriptionfactory.dto.SDDocumentDto;
import net.catenax.selfdescriptionfactory.repo.VCModel;
import net.catenax.selfdescriptionfactory.repo.VerifiableCredentialRepo;
import net.catenax.selfdescriptionfactory.service.wallet.CustodianWallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A service to create and manipulate of Self-Description document
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@RequiredArgsConstructor
public class SDFactory {

    // Namespace for the Self-Description document context
    static final URI SD_VOC_URI = URI.create("https://df2af0fe-d34a-4c48-abda-c9cdf5718b4a.mock.pstmn.io/sd-document-v0.1.jsonld");
    // Namespace for the Traceability context, used for the test in conjunction with https://catalog.demo.supplytree.org project
    static final URI TRACEABILITY_VOC_URI = URI.create("https://w3id.org/traceability/v1");

    public static final Pattern UUID_REGEX = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");

    static {
        try (InputStream sdVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/sd-document-v0.1.jsonld");
             InputStream trVocIs = SDFactory.class.getClassLoader().getResourceAsStream("verifiablecredentials.jsonld/traceability-v1.jsonld")) {
            assert sdVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(SD_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, sdVocIs));
            assert trVocIs != null;
            VerifiableCredentialContexts.CONTEXTS.put(TRACEABILITY_VOC_URI, JsonDocument.of(com.apicatalog.jsonld.http.media.MediaType.JSON_LD, trVocIs));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Value("${app.verifiableCredentials.durationDays:90}")
    private int duration;
    @Value("${app.verifiableCredentials.idPrefix}")
    private String idPrefix;

    private final VerifiableCredentialRepo vcRepo;
    private final CustodianWallet custodianWallet;

    /**
     * Stores VerifiableCredential in Mongo DB without checks
     *
     * @param verifiableCredential credential to be saved
     * @param sdDocumentDto
     */
    public void storeVC(VerifiableCredential verifiableCredential, SDDocumentDto sdDocumentDto, String id) {
        var vcModel = VCModel.builder()
                .id(id)
                .bpn(sdDocumentDto.getBpn())
                .companyNumber(sdDocumentDto.getCompany_number())
                .headquarterCountry(sdDocumentDto.getHeadquarter_country())
                .legalCountry(sdDocumentDto.getLegal_country())
                .serviceProvider(sdDocumentDto.getService_provider())
                .sdType(sdDocumentDto.getSd_type())
                .fullJson(verifiableCredential.toJson())
                .build();
        vcRepo.save(vcModel);
    }

    /**
     * Creates a VerifiableCredential on base of provided claims
     *
     * @param claims    claims to be included to the VerifiableCredentials
     * @param holderBpn DID or BPN of the Holder for given claims
     * @param issuerBpn DID or BPN of the Issuer for the signature
     * @return VerifiableCredential signed by CatenaX authority
     */
    public VerifiableCredential createVC(String id, Map<String, Object> claims, String holderBpn, String issuerBpn) {
        CredentialSubject credentialSubject = CredentialSubject.builder()
                .claims(claims)
                .build();
        Date issuanceDate = new Date();
        VerifiableCredential verifiableCredential = VerifiableCredential.builder()
                .context(SD_VOC_URI)
                .id(UriComponentsBuilder.fromUriString(idPrefix).path("/selfdescription/vc/" + id).build().toUri())
                .issuanceDate(issuanceDate)
                .expirationDate(Date.from(Instant.now().plus(Duration.ofDays(duration))))
                .type("SD-document")
                .credentialSubject(credentialSubject)
                .build();
        JsonLDUtils.jsonLdAdd(verifiableCredential, "issuerIdentifier", issuerBpn);
        JsonLDUtils.jsonLdAdd(verifiableCredential, "holderIdentifier", holderBpn);

        return custodianWallet.getSignedVC(verifiableCredential);
    }

    /***
     * Deletes Verifiable Credentials from DB
     * @param ids list of VC identities
     */
    public void removeSelfDescriptions(List<String> ids) {
        vcRepo.deleteAllById(normalizeIds(ids));
    }

    private List<String> normalizeIds(List<String> ids) {
        return ids.stream()
                .map(it -> {
                    var matcher = UUID_REGEX.matcher(it);
                    if (matcher.find()) {
                        return matcher.group(0);
                    }
                    return it;
                })
                .collect(Collectors.toList());
    }
}
