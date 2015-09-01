package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name="key_store")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class KeyStore extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9140771929158523900L;

	@Column(name = "key_value")
	@Type(type = "StringJsonObject")
	private String keyValue;
	
	@Column(name = "ex_system")
	private String exSystem;

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public String getExSystem() {
		return exSystem;
	}

	public void setExSystem(String exSystem) {
		this.exSystem = exSystem;
	}
	
}
