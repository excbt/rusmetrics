package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractKeynameEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4290975733365393813L;

	
	@Id
	@Column(name = "keyname")
	private String keyname;


	public String getKeyname() {
		return keyname;
	}


	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	
}
