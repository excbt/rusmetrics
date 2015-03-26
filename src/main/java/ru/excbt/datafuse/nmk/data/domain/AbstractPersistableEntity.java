package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Inspired by org.springframework.data.jpa.domain.AbstractPersistable
 * 
 *
 * @param <PK>
 */
@MappedSuperclass
public abstract class AbstractPersistableEntity <PK extends Serializable> implements Persistable<PK> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6201049315760221599L;

	@Id
	@SequenceGenerator(name = "abstractEntity", sequenceName = "seq_global_id", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "abstractEntity")
	@Column
	private PK id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#getId()
	 */	
	@Override
	public PK getId() {
		return id;
	}

	/**
	 * Sets the id of the entity.
	 * 
	 * @param id the id to set
	 */	
	public void setId(final PK id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Persistable#isNew()
	 */
	@Override
	@JsonIgnore
	public boolean isNew() {
		return null == getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */	
	@Override
	public String toString() {
		return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */	
	@Override
	public int hashCode() {
		int hashCode = 17;

		hashCode += null == getId() ? 0 : getId().hashCode() * 31;

		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */	
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		AbstractPersistableEntity<?> that = (AbstractPersistableEntity<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}
	
	
}
