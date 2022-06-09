package net.catenax.sdhub.service;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.repo.VCModel;
import net.catenax.sdhub.repo.VerifiableCredentialRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A query interface implementation of SD-hub
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DBService {
    private final VerifiableCredentialRepo vcRepo;
    private final SDHub sdHub;

    public static final Pattern UUID_REGEX = Pattern.compile("\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}");

    /**
     * Searches the VerifiableCredentials by the parameter to include them to the VerifiablePresentation
     *
     * @param ids                  Holder identities
     * @param companyNumbers       query parameter
     * @param headquarterCountries query parameter
     * @param legalCountries       query parameter
     * @param bpns                 query parameter
     * @return Verifiable Presentation
     */
    public VerifiablePresentation getSelfDescriptions(List<String> ids, List<String> companyNumbers,
                                                      List<String> headquarterCountries, List<String> legalCountries,
                                                      List<String> serviceProviders, List<String> sdTypes,
                                                      List<String> bpns) {
        ids = normalizeIds(ids);
        var vcs = vcRepo.findAllByIdInAndCompanyNumberInAndHeadquarterCountryInAndLegalCountryInAndServiceProviderInAndSdTypeInAndBpnIn(
                ids, companyNumbers, headquarterCountries, legalCountries, serviceProviders, sdTypes, bpns
        );

        return retriveVp(vcs);
    }

    /**
     * Searches the VerifiableCredentials by the parameter to include them to the VerifiablePresentation
     *
     * @param ids VC identities
     * @return Verifiable Presentation
     */
    public VerifiablePresentation getSelfDescriptions(List<String> ids) {
        var vcs = vcRepo.findAllByIdIn(normalizeIds(ids));
        return retriveVp(vcs);
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

    private VerifiablePresentation retriveVp(List<VCModel> vcs) {
        var res = vcs
                .stream()
                .map(it -> VerifiableCredential.fromJson(it.getFullJson()))
                .collect(Collectors.toList());
        try {
            return sdHub.createVP(res);
        } catch (Exception e) {
            throw new RuntimeException("Exception during creating VP", e);
        }
    }

    /**
     * Searches the VerifiableCredential by the id
     *
     * @param id VC id
     * @return Verifiable Presentation
     */
    public VerifiableCredential getVc(String id) {
        var vcs = vcRepo.findAllByIdIn(normalizeIds(List.of(id)));
        if (!vcs.isEmpty()) {
            return VerifiableCredential.fromJson(vcs.get(0).getFullJson());
        } else {
            return null;
        }
    }
}