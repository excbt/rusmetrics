package ru.excbt.datafuse.nmk.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.filters.PropertyFilter;

@JsonInclude(value = Include.USE_DEFAULTS)
@JsonIgnoreProperties(value = { PropertyFilter.DEV_COMMENT_PROPERTY_IGNORE, "deleted" }, ignoreUnknown = true)
public class JsonAbstractKeynameEntity extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -723796139761588540L;

}
