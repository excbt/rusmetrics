package ru.excbt.datafuse.nmk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.domain.OrganizationType;

import java.util.Optional;

@Repository
public interface OrganizationTypeRepository extends JpaRepository<OrganizationType, Long> {

}
