package net.catenax.sdhub.service;

import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.catenax.sdhub.repo.DBVCEntity;
import org.springframework.stereotype.Service;

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
    private final DBVCRepoService vcRepoService;
    private final SDHub sdHub;
    private final ObjectMapper objectMapper;

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
        var vcs = vcRepoService.findByParams(
                        ids, companyNumbers, headquarterCountries, legalCountries, serviceProviders, sdTypes, bpns
                )
                .stream()
                .map(DBVCEntity::getVc)
                .toList();

        return retriveVp(vcs);
    }

    /**
     * Searches the VerifiableCredentials by the parameter to include them to the VerifiablePresentation
     *
     * @param ids VC identities
     * @return Verifiable Presentation
     */
    public VerifiablePresentation getSelfDescriptions(List<String> ids) {
        var vcs = vcRepoService.findByIdIn(normalizeIds(ids))
                .stream()
                .map(DBVCEntity::getVc)
                .toList();
        return retriveVp(vcs);
    }

    private VerifiablePresentation retriveVp(List<String> vcs) {
        var res = vcs
                .stream()
                .map(VerifiableCredential::fromJson)
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
        var vcs = vcRepoService.findByIdIn(normalizeIds(List.of(id)))
                .stream()
                .map(DBVCEntity::getVc)
                .toList();
        if (!vcs.isEmpty()) {
            var jsn = "";
            try {
                jsn = objectMapper.writeValueAsString(vcs.get(0));
            } catch (JsonProcessingException e) {
                return null;
            }
            return VerifiableCredential.fromJson(jsn);
        } else {
            return null;
        }
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
