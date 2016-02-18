package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ReportTask;

/**
 * Repository для ReportTask
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.04.2015
 *
 */
public interface ReportTaskRepository extends CrudRepository<ReportTask, Long> {

}
