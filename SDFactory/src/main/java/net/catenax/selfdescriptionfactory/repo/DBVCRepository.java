package net.catenax.selfdescriptionfactory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DBVCRepository extends JpaRepository<DBVCEntity, UUID> {
    @Modifying
    @Query(nativeQuery = true, value = "delete from verifiable_credential where vc ->'credential_subject'->>'id' in (:ids)")
    List<DBVCEntity> deleteByIds(@Param("ids") String ids);
}
