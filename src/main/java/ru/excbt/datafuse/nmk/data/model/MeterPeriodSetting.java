/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 * 
 */

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "meter_period_setting")
@Getter
@Setter
public class MeterPeriodSetting extends AbstractAuditableModel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 7750787101754538870L;

	@Column(name = "subscriber_id")
	private Long subscriberId;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "dev_comment")
	private String devComment;

	@Column(name = "from_day")
	private Integer fromDay;

	@Column(name = "to_day")
	private Integer toDay;

	@Column(name = "value_count")
	private Integer valueCount;

	@Column(name = "value_type_prefix")
	private String valueTypePrefix;

	@Column(name = "version")
	private int version;

}