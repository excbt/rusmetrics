package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColor;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;

import java.util.List;

/**
 *
 */
public interface ContEventLevelColorV2Repository extends JpaRepository<ContEventLevelColorV2, String> {

}
