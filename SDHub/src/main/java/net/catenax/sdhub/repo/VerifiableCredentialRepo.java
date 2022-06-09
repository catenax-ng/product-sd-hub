package net.catenax.sdhub.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerifiableCredentialRepo extends JpaRepository<VCModel, String> {
    List<VCModel> findAllByIdInAndCompanyNumberInAndHeadquarterCountryInAndLegalCountryInAndServiceProviderInAndSdTypeInAndBpnIn(List<String> id,
                                                                                                                                 List<String> companyNumber,
                                                                                                                                 List<String> headquarterCountry,
                                                                                                                                 List<String> legalCountry,
                                                                                                                                 List<String> serviceProvider,
                                                                                                                                 List<String> sdType,
                                                                                                                                 List<String> bpn);

    List<VCModel> findAllByIdIn(List<String> id);
}