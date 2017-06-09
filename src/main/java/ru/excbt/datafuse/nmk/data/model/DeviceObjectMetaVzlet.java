package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

@Entity
@Table(name = "device_object_meta_vzlet")
@Getter
@Setter
public class DeviceObjectMetaVzlet extends AbstractAuditableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 2778200912535462611L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_object_id")
	@JsonIgnore
	private DeviceObject deviceObject;

	@Column(name = "device_object_id", insertable = false, updatable = false)
	@JsonIgnore
	private Long deviceObjectId;

	@Column(name = "vzlet_table_hour")
	private String vzletTableHour;

	@Column(name = "vzlet_table_day")
	private String vzletTableDay;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id1", insertable = false, updatable = false)
	private VzletSystem vzletSystem1;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id2", insertable = false, updatable = false)
	private VzletSystem vzletSystem2;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "vzlet_system_id3", insertable = false, updatable = false)
	private VzletSystem vzletSystem3;

	@Column(name = "vzlet_system_id1")
	private Long vzletSystemId1;

	@Column(name = "vzlet_system_id2")
	private Long vzletSystemId2;

	@Column(name = "vzlet_system_id3")
	private Long vzletSystemId3;

	@Column(name = "exclude_nulls")
	@NotNull
	private Boolean excludeNulls;

	@Column(name = "meta_props_only")
	@NotNull
	private Boolean metaPropsOnly;

	@Version
	private int version;

}
