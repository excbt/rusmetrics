package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(name = "timezone_def")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
@Immutable
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

}
