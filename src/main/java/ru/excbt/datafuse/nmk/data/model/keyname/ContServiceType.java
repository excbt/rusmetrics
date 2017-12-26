package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;

@Entity
@Table(name = "cont_service_type")
@JsonInclude(Include.NON_NULL)
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class ContServiceType extends AbstractKeynameEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 6558062018028025474L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "cont_service_type_name")
	private String name;

	@Column(name = "cont_service_type_comment")
	private String comment;

	@JsonIgnore
	@Column(name = "ex_code")
	private String exCode;

	@JsonIgnore
	@Version
	private int version;

	@Column(name = "service_order")
	private Integer serviceOrder;

	@Column(name = "caption_short")
	private String captionShort;


    public String getCaption() {
        return caption;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getExCode() {
        return exCode;
    }

    public int getVersion() {
        return version;
    }

    public Integer getServiceOrder() {
        return serviceOrder;
    }

    public String getCaptionShort() {
        return captionShort;
    }
}
