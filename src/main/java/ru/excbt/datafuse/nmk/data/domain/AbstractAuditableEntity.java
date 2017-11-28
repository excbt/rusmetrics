package ru.excbt.datafuse.nmk.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

/**
 * Базовый абстрактный класс сущности с аудитом
 *
 *
 * @version 1.0
 * @since 12.03.2015
 *
 * @param <PK>
 */
@MappedSuperclass
public abstract class AbstractAuditableEntity<PK extends Serializable> extends AbstractPersistableEntity<PK> {

	private static final long serialVersionUID = -4282498146105728631L;

    @CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	@JsonIgnore
	private Long createdBy;

    @CreatedDate
	@Column(name = "created_date", updatable = false, nullable = false)
	@JsonIgnore
	private Instant createdDate;

    @LastModifiedBy
	@Column(name = "last_modified_by")
	@JsonIgnore
	private Long lastModifiedBy;

    @LastModifiedDate
	@Column(name = "last_modified_date")
	@JsonIgnore
	private Instant lastModifiedDate;

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "AbstractAuditableEntity{" +
            "createdBy=" + createdBy +
            ", createdDate=" + createdDate +
            ", lastModifiedBy=" + lastModifiedBy +
            ", lastModifiedDate=" + lastModifiedDate +
            "} " + super.toString();
    }
}
