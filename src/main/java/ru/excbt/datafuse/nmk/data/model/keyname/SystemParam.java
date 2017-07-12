package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "system_param")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
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

}
