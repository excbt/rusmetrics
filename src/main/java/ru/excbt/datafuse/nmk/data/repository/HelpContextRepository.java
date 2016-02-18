package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.HelpContext;

/**
 * Repository для HelpContext
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.02.2016
 *
 */
public interface HelpContextRepository extends CrudRepository<HelpContext, Long> {

	/**
	 * 
	 * @param anchorId
	 * @return
	 */
	public List<HelpContext> findByAnchorId(String anchorId);

}
