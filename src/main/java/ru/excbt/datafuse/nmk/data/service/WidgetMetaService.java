/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Widget;
import ru.excbt.datafuse.nmk.data.repository.WidgetRepository;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 * 
 */
@Service
public class WidgetMetaService {

	@Inject
	private WidgetRepository widgetRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<String> selectAllWidgetsJson() {
		return widgetRepository.findAll().stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.map(Widget::toJsonSilent).collect(Collectors.toList());
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Widget> selectAllWidgets() {
		return widgetRepository.findAll().stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.collect(Collectors.toList());
	}

}
