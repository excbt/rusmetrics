package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DevModeObject;
import ru.excbt.datafuse.nmk.data.model.markers.DisabledObject;

@Entity
@Table(name = "report_type")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class ReportType extends AbstractKeynameEntity implements DevModeObject, DisabledObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 6608655097029684171L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "report_type_name")
	private String name;

	@Column(name = "report_type_description")
	private String description;

	@Column(name = "report_type_comment")
	private String comment;

	@Column(name = "report_type_suffix")
	private String suffix;

	@Column(name = "report_type_enabled")
	private boolean _enabled;

	@Column(name = "is_dev_mode")
	private Boolean isDevMode;

	@Column(name = "preview_url")
	private String previewUrl;

	@Version
	private int version;

	@Column(name = "report_type_order")
	private Integer reportTypeOrder;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Column(name = "report_system")
	private String reportSystem;

	@Column(name = "report_category")
	private String reportCategory;

	@Column(name = "resource_category")
	private String resourceCategory;


}
