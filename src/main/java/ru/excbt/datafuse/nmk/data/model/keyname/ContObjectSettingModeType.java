package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "cont_object_setting_mode_type")
@Where(clause="deleted = 0 and enabled")
public class ContObjectSettingModeType extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2338801306280932588L;

	@Column(name = "caption")
	private String caption;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
}
