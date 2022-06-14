package net.catenax.sdhub.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DBVCRepository extends JpaRepository<DBVCEntity, UUID> {
    @Query(nativeQuery = true, value = """
            select * from verifiable_credential where 
                (:ids like '' or vc ->'credential_subject'->>'id' in (:ids)) and
                (:companyNums like '' or vc ->'credential_subject'->>'company_number' in (:companyNums)) and
                (:headquarterCountries like '' or vc ->'credential_subject'->>'headquarter_country' in (:headquarterCountries)) and
                (:legalCountries like '' or vc ->'credential_subject'->>'legal_country' in (:legalCountries)) and
                (:serviceProviders like '' or vc ->'credential_subject'->>'service_provider' in (:serviceProviders)) and
                (:sdType like '' or vc ->'credential_subject'->>'sd_type' in (:sdType)) and
                (:bpns like '' or vc ->'credential_subject'->>'bpn' = any(string_to_array(:bpns, ',')))
            """)
    List<DBVCEntity> findByParams(
            @Param("ids") String ids,
            @Param("companyNums") String companyNums,
            @Param("headquarterCountries") String headquarterCountries,
            @Param("legalCountries") String legalCountries,
            @Param("serviceProviders") String serviceProviders,
            @Param("sdType") String sdType,
            @Param("bpns") String bpns
    );

    @Query(nativeQuery = true, value = "select * from verifiable_credential where :ids like '' or vc ->'credential_subject'->>'id' in (:ids)")
    List<DBVCEntity> findByIdIn(@Param("ids") String ids);
}
