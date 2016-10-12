/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_impulse_counter_type")
public class ImpulseCounterType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5974818248757497711L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@JsonIgnore
	@Column(name = "driver_name")
	private String driverName;

	@JsonIgnore
	@Column(name = "order_idx")
	private Integer orderIdx;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public Integer getOrderIdx() {
		return orderIdx;
	}

	public void setOrderIdx(Integer orderIdx) {
		this.orderIdx = orderIdx;
	}

}