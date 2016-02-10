package ru.excbt.datafuse.nmk.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

/**
 * Тепло системы Взлет
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.mm.2015
 *
 */
@Entity
@Table(name = "vzlet_system")
public class VzletSystem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 244069496621673974L;

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "system_name")
	private String systemName;

	@Column(name = "system_caption")
	private String systemCaption;

	@Column(name = "system_description")
	private String systemDescription;

	@Column(name = "system_comment")
	private String systemComment;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cont_service_type", insertable = false, updatable = false)
	private ContServiceType contServiceType;

	@Column(name = "cont_service_type")
	private String contServiceTypeKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemCaption() {
		return systemCaption;
	}

	public void setSystemCaption(String systemCaption) {
		this.systemCaption = systemCaption;
	}

	public String getSystemDescription() {
		return systemDescription;
	}

	public void setSystemDescription(String systemDescription) {
		this.systemDescription = systemDescription;
	}

	public String getSystemComment() {
		return systemComment;
	}

	public void setSystemComment(String systemComment) {
		this.systemComment = systemComment;
	}

	public ContServiceType getContServiceType() {
		return contServiceType;
	}

	public void setContServiceType(ContServiceType contServiceType) {
		this.contServiceType = contServiceType;
	}

	public String getContServiceTypeKey() {
		return contServiceTypeKey;
	}

	public void setContServiceTypeKey(String contServiceTypeKey) {
		this.contServiceTypeKey = contServiceTypeKey;
	}

}
