package net.catenax.selfdescriptionfactory.repo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RepoCredentialSubject implements Serializable {
    @Column(name = "id")
    private String id;

    @Column(name = "company_number")
    @JsonProperty("company_number")
    private String companyNumber;

    @Column(name = "headquarter_country")
    @JsonProperty("headquarter_country")
    private String headquarterCountry;

    @Column(name = "legal_country")
    @JsonProperty("legal_country")
    private String legalCountry;

    @Column(name = "service_provider")
    @JsonProperty("service_provider")
    private String serviceProvider;

    @Column(name = "sd_type")
    @JsonProperty("sd_type")
    private String sdType;

    @Column(name = "bpn")
    private String bpn;
}
