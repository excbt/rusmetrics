package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "cont_zpoint_setting_mode_check")
@Where(clause="deleted = 0 and enabled")
public class ContZPointSettingModeCheck extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2338801306280932588L;

}
