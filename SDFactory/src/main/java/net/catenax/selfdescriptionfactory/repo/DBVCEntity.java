package net.catenax.selfdescriptionfactory.repo;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "verifiable_credential")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class DBVCEntity {
    @Id
    private UUID id;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private VCModel vc;
}
