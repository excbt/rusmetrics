package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "tariff_option")
public class TariffOption extends AbstractKeynameEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6014135039180327715L;

 
	@Column (name = "tariff_option_name")
	private String tariffOptionName;

	@Column (name = "tariff_option_description")
	private String tariffOptionDescription;
	
	@Column (name = "tariff_option_comment")
	private String tariffOptionComment;

	@Column(name = "tariff_option_order")
	private int tariffOptionOrder;
	
	
	public String getTariffOptionName() {
		return tariffOptionName;
	}

	public void setTariffOptionName(String tariffOptionName) {
		this.tariffOptionName = tariffOptionName;
	}

	public String getTariffOptionDescription() {
		return tariffOptionDescription;
	}

	public void setTariffOptionDescription(String tariffOptionDescription) {
		this.tariffOptionDescription = tariffOptionDescription;
	}

	public String getTariffOptionComment() {
		return tariffOptionComment;
	}

	public void setTariffOptionComment(String tariffOptionComment) {
		this.tariffOptionComment = tariffOptionComment;
	}

	public int getTariffOptionOrder() {
		return tariffOptionOrder;
	}

	public void setTariffOptionOrder(int tariffOptionOrder) {
		this.tariffOptionOrder = tariffOptionOrder;
	}
	
}
