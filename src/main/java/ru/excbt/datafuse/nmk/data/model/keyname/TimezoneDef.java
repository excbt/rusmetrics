package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "timezone_def")
public class TimezoneDef extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 341837575197941958L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "timezone_name")
	private String timezoneName;

	@Column(name = "timezone_description")
	private String timezoneDescription;

	@Column(name = "canonical_id")
	private String cononicalId;

	@Column(name = "timezone_is_default")
	private Boolean isDefault;

	public String getCaption() {
		return caption;
	}

	public String getTimezoneName() {
		return timezoneName;
	}

	public String getTimezoneDescription() {
		return timezoneDescription;
	}

	public String getCononicalId() {
		return cononicalId;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

}
