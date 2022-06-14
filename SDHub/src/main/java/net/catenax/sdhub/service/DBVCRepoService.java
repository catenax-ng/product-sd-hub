package net.catenax.sdhub.service;

import lombok.RequiredArgsConstructor;
import net.catenax.sdhub.repo.DBVCEntity;
import net.catenax.sdhub.repo.DBVCRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DBVCRepoService {
    private final DBVCRepository repo;

    public List<DBVCEntity> findByParams(List<String> ids,
                                         List<String> companyNums,
                                         List<String> headquarterCountries,
                                         List<String> legalCountries,
                                         List<String> serviceProviders,
                                         List<String> sdType,
                                         List<String> bpns) {
        var idsParam = formatList(ids);
        var companyNumsParam = formatList(companyNums);
        var headquarterCountriesParam = formatList(headquarterCountries);
        var legalCountriesParam = formatList(legalCountries);
        var serviceProvidersParam = formatList(serviceProviders);
        var sdTypeParam = formatList(sdType);
        var bpnsParam = formatList(bpns);

        return repo.findByParams(idsParam, companyNumsParam, headquarterCountriesParam, legalCountriesParam, serviceProvidersParam, sdTypeParam, bpnsParam);
    }

    public List<DBVCEntity> findByIdIn(List<String> ids) {
        return repo.findByIdIn(formatList(ids));
    }

    private String formatList(List<String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return String.join(",", params);
    }
}
