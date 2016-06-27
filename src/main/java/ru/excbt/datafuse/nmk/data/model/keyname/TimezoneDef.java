package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(name = "timezone_def")
@JsonInclude(Include.NON_NULL)
public class TimezoneDef extends AbstractKeynameEntity implements DeletedMarker {

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

	@JsonIgnore
	@Column(name = "canonical_id")
	private String cononicalId;

	@JsonIgnore
	@Column(name = "timezone_is_default")
	private Boolean isDefault;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@JsonIgnore
	@Column(name = "timezone_at_offset")
	private String timezoneAtOffset;

	@Column(name = "timezone_suffix")
	private String timezoneSuffix;

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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int getDeleted() {
		return deleted;
	}

	public String getTimezoneAtOffset() {
		return timezoneAtOffset;
	}

	public String getTimezoneSuffix() {
		return timezoneSuffix;
	}

}
