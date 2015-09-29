package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

@MappedSuperclass
public abstract class AbstractKeynameEntity implements Serializable, KeynameObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4290975733365393813L;

	
	@Id
	@Column(name = "keyname")
	private String keyname;


	@Override
	public String getKeyname() {
		return keyname;
	}


	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyname == null) ? 0 : keyname.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractKeynameEntity other = (AbstractKeynameEntity) obj;
		if (keyname == null) {
			if (other.keyname != null)
				return false;
		} else if (!keyname.equals(other.keyname))
			return false;
		return true;
	}

	
}
