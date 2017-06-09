package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;
import ru.excbt.datafuse.nmk.data.model.types.ContEventLevelColorKey;

@Deprecated
@Entity
@Table(name = "cont_event_level_color")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class ContEventLevelColor extends AbstractKeynameEntity implements
		StatusColorObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -406404451942425879L;

	@Column(name = "keyname", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	private ContEventLevelColorKey colorKey;

	@Column(name = "caption")
	private String caption;

	@Column(name = "color_description")
	@JsonIgnore
	private String colorDescription;

	@Column(name = "color_rank")
	private Integer colorRank;

	@Column(name = "is_bad_color")
	private Boolean isBadColor;

	@Column(name = "is_base_color")
	private Boolean isBaseColor;

	@Column(name = "is_critical_color")
	private Boolean isCriticalColor;

	@Column(name = "cont_event_level_from")
	@JsonIgnore
	private Integer levelFrom;

	@Column(name = "cont_event_level_to")
	@JsonIgnore
	private Integer levelTo;

	@Override
	public String getStatusColor() {
		return this.getKeyname();
	}

}
