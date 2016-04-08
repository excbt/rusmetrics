package ru.excbt.datafuse.nmk.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.filters.PropertyFilter;

/**
 * 
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 08.04.2016
 *
 */
//@JsonInclude(value = Include.NON_NULL)
@JsonInclude(value = Include.USE_DEFAULTS)
@JsonIgnoreProperties(value = { PropertyFilter.DEV_COMMENT_PROPERTY_IGNORE, "deleted" }, ignoreUnknown = true)
public class JsonAbstractAuditableModel extends AbstractAuditableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2361272867854609918L;

}
