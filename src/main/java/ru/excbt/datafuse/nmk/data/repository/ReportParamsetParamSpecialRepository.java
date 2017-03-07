package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportParamsetParamSpecial;

/**
 * Repository для ReportParamsetParamSpecial
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.06.2015
 *
 */
public interface ReportParamsetParamSpecialRepository extends JpaRepository<ReportParamsetParamSpecial, Long> {

}
