/**
 *
 */
package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_impulse_counter_type")
@Getter
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

}
