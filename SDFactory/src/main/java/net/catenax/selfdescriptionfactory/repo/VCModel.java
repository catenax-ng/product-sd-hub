package net.catenax.selfdescriptionfactory.repo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VCModel {
    @Column(name = "context")
    @JsonProperty("@context")
    private List<String> context;

    @Column(name = "type")
    private List<String> type;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "issuanceDate")
    private Date issuanceDate;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "credential_subject")
    @JsonProperty("credentialSubject")
    private RepoCredentialSubject credentialSubject;
}
