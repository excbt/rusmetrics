package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "tariff_option")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class TariffOption extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = -6014135039180327715L;

	@Column(name = "tariff_option_name")
	private String tariffOptionName;

	@Column(name = "tariff_option_description")
	private String tariffOptionDescription;

	@Column(name = "tariff_option_comment")
	private String tariffOptionComment;

	@Column(name = "tariff_option_order")
	private Integer tariffOptionOrder;

}
