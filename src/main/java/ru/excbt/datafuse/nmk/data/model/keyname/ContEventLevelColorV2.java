package ru.excbt.datafuse.nmk.data.model.keyname;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.StatusColorObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_event_level_color_v2")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContEventLevelColorV2 extends AbstractKeynameEntity implements
		StatusColorObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -406404451942425879L;

	@Column(name = "keyname", insertable = false, updatable = false)
	private String colorKey;

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

	public ContEventLevelColorV2 keyname(String keyname) {
	    this.setKeyname(keyname);
	    return this;
    }

    public String getColorKey() {
        return this.colorKey;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getColorDescription() {
        return this.colorDescription;
    }

    public Integer getColorRank() {
        return this.colorRank;
    }

    public Boolean getIsBadColor() {
        return this.isBadColor;
    }

    public Boolean getIsBaseColor() {
        return this.isBaseColor;
    }

    public Boolean getIsCriticalColor() {
        return this.isCriticalColor;
    }

    public Integer getLevelFrom() {
        return this.levelFrom;
    }

    public Integer getLevelTo() {
        return this.levelTo;
    }
}
