/**
 *
 */
package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.support.ContObject2RI;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 *
 */
public interface ContObjectGeoPosRepository extends JpaRepository<ContObjectGeoPos, Long>, ContObject2RI<ContObjectGeoPos> {

}
