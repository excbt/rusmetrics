/**
 *
 */
package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_service_data_hwater_import")
@NamedStoredProcedureQueries({ @NamedStoredProcedureQuery(name = "importData",
		procedureName = "portal.process_service_data_hwater_import", parameters = {
				@StoredProcedureParameter(mode = ParameterMode.IN, name = "par_session_uuid", type = String.class) }) })
@Getter
@Setter
public class ContServiceDataHWaterImport extends AbstractPersistableEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = -9047907605001865566L;

	@Column(name = "data_date")
	private Date dataDate;

	@Column(name = "device_object_id")
	@JsonIgnore
	private Long deviceObjectId;

	@Column(name = "cont_zpoint_id")
	@JsonIgnore
	private Long contZPointId;

	@Column(name = "time_detail_type")
	private String timeDetailType;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column
	private BigDecimal t_in;

	@Column
	private BigDecimal t_out;

	@Column
	private BigDecimal t_cold;

	@Column
	private BigDecimal t_outdoor;

	@Column
	private BigDecimal m_in;

	@Column
	private BigDecimal m_out;

	@Column
	private BigDecimal m_delta;

	@Column
	private BigDecimal v_in;

	@Column
	private BigDecimal v_out;

	@Column
	private BigDecimal v_delta;

	@Column
	private BigDecimal h_in;

	@Column
	private BigDecimal h_out;

	@Column
	private BigDecimal h_delta;

	@Column
	private BigDecimal p_in;

	@Column
	private BigDecimal p_out;

	@Column
	private BigDecimal p_delta;

	@Column(name = "work_time")
	private BigDecimal workTime;

	@Column(name = "fail_time")
	private BigDecimal failTime;

	//	@Column(name = "crc32_value", insertable = false, updatable = false)
	//	private Integer crc32Value;

	@Column(name = "crc32_valid", insertable = false, updatable = false)
	private Boolean crc32Valid;

	@Column(name = "data_mstatus")
	private Short dataMstatus;

	@Column(name = "data_changed", insertable = false, updatable = false)
	private Boolean dataChanged;

	@JsonIgnore
	@Column(name = "trx_id")
	@org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
	private UUID trxId;

	@Column(name = "created_by", updatable = false)
	@JsonIgnore
	private Long createdBy;

	@Column(name = "created_date", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdDate;

}
