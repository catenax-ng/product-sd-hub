package net.catenax.selfdescriptionfactory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerifiableCredentialRepo extends JpaRepository<VCModel, String> {
}