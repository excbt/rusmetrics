/**
 *
 */
package ru.excbt.datafuse.nmk.data.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;
import ru.excbt.datafuse.nmk.data.model.markers.DataDateFormatter;
import ru.excbt.datafuse.nmk.utils.DateFormatUtils;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 10.10.2016
 *
 */
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "cont_service_data_impulse")
@Getter
@Setter
public class ContServiceDataImpulse extends AbstractAuditableModel implements DataDateFormatter {

	/**
	 *
	 */
	private static final long serialVersionUID = -147661728148659523L;

	@Column(name = "data_date", updatable = false)
	private Date dataDate;

	@Column(name = "cont_zpoint_id", updatable = false)
	private Long contZpointId;

	@Column(name = "device_object_id", updatable = false)
	private Long deviceObjectId;

	@Column(name = "time_detail_type", updatable = false)
	private String timeDetailType;

	@Column(name = "data_value", columnDefinition = "numeric")
	private Double dataValue;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

    @JsonProperty
    public Date getInsertDate() {
        Instant dateTime = super.getCreatedDate();
        return dateTime != null ? Date.from(dateTime) : null;
    }

    @JsonProperty
    public String getInsertDateStr() {
        Instant dateTime = super.getCreatedDate();
        return dateTime != null ? DateFormatUtils.formatDateTime(Date.from(dateTime), DateFormatUtils.DATE_FORMAT_STR_FULL) : null;
    }



}
