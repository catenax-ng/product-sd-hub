package net.catenax.selfdescriptionfactory.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "verifiable_credential")
public class VCModel {
    @Id
    private String id;

    @Column(name = "company_number")
    private String companyNumber;

    @Column(name = "headquarter_country")
    private String headquarterCountry;

    @Column(name = "legal_country")
    private String legalCountry;

    @Column(name = "service_provider")
    private String serviceProvider;

    @Column(name = "sd_type")
    private String sdType;

    @Column(name = "bpn")
    private String bpn;

    @Column(name = "full_json", length = 100_000)
    private String fullJson;
}
