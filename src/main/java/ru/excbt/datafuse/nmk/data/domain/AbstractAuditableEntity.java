package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Inspired by org.springframework.data.jpa.domain.AbstractAuditable
 *
 * @param <U>
 * @param <PK>
 */
@MappedSuperclass
public abstract class AbstractAuditableEntity<U, PK extends Serializable> extends
		AbstractPersistableEntity<PK> implements Auditable<U, PK> {

	/**
	 * Abstract base class for entities. Allows parameterization of id type, chooses auto-generation and implements
	 * {@link #equals(Object)} and {@link #hashCode()} based on that id.
	 * 
	 * @author Oliver Gierke
	 * @param <PK> the the of the entity
	 */	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4282498146105728631L;

	@ManyToOne
	@JoinColumn(name = "created_by")	
	@JsonIgnore
	private U createdBy;

	@Column(name = "created_date")	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date createdDate;
		
	@ManyToOne
	@JoinColumn(name = "last_modified_by")
	@JsonIgnore
	private U lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_modified_date")
	@JsonIgnore
	private Date lastModifiedDate;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Auditable#getCreatedBy()
	 */
	@Override
	public U getCreatedBy() {

		return createdBy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
	 */
	@Override	
	public void setCreatedBy(final U createdBy) {

		this.createdBy = createdBy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
	@Override	
	public DateTime getCreatedDate() {

		return null == createdDate ? null : new DateTime(createdDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.domain.Auditable#setCreatedDate(org.joda.time
	 * .DateTime)
	 */
	@Override	
	public void setCreatedDate(final DateTime createdDate) {

		this.createdDate = null == createdDate ? null : createdDate.toDate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
	 */
	@Override	
	public U getLastModifiedBy() {

		return lastModifiedBy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang
	 * .Object)
	 */
	@Override	
	public void setLastModifiedBy(final U lastModifiedBy) {

		this.lastModifiedBy = lastModifiedBy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
	@Override	
	public DateTime getLastModifiedDate() {

		return null == lastModifiedDate ? null : new DateTime(lastModifiedDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.domain.Auditable#setLastModifiedDate(org.joda
	 * .time.DateTime)
	 */
	@Override	
	public void setLastModifiedDate(final DateTime lastModifiedDate) {

		this.lastModifiedDate = null == lastModifiedDate ? null : lastModifiedDate.toDate();
	}

}
