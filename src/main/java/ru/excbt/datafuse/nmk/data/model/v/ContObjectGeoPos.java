package ru.excbt.datafuse.nmk.data.model.v;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;
import ru.excbt.datafuse.nmk.data.model.ContObject;

/**
 * Данные по координатам для объекта учета
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 29.07.2015
 *
 */
@Entity
@Subselect("select * from v_cont_object_geo_pos_xy")
@JsonInclude(Include.NON_NULL)
@Getter
public class ContObjectGeoPos implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7360034990378154122L;

	@Id
	@Column(name = "cont_object_id", insertable = false, updatable = false)
	private Long contObjectId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id")
	@JsonIgnore
	private ContObject contObject;

	@Column(name = "geo_pos_x", columnDefinition = "numeric")
	private Double geoPosX;

	@Column(name = "geo_pos_y", columnDefinition = "numeric")
	private Double geoPosY;

	@Column(name = "city_geo_pos_x", columnDefinition = "numeric")
	private Double cityGeoPosX;

	@Column(name = "city_geo_pos_y", columnDefinition = "numeric")
	private Double cityGeoPosY;

}
