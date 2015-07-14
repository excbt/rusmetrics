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

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getTimezoneName() {
		return timezoneName;
	}

	public void setTimezoneName(String timezoneName) {
		this.timezoneName = timezoneName;
	}

	public String getTimezoneDescription() {
		return timezoneDescription;
	}

	public void setTimezoneDescription(String timezoneDescription) {
		this.timezoneDescription = timezoneDescription;
	}
	
	
}
