package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "system_param")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SystemParam extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 896311233564021338L;

	@Column(name = "param_name")
	private String paramName;

	@Column(name = "param_value")
	private String paramValue;

	@Column(name = "param_type")
	private String paramType;

	@Column(name = "param_group")
	private String paramGroup;

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamGroup() {
		return paramGroup;
	}

	public void setParamGroup(String paramGroup) {
		this.paramGroup = paramGroup;
	}


}
