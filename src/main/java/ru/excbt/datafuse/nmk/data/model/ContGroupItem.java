package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

/**
 * Элементы группы
 * 
 * @author S.Kuzovoy
 * @version 1.0
 * @since 27.05.2015
 *
 */
@Entity
@Table(name = "cont_group_item")
public class ContGroupItem extends AbstractAuditableModel {

	/**
		 * 
		 */
	private static final long serialVersionUID = 6212870140304523057L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_group_id")
	private ContGroup contGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cont_object_id", insertable = false, updatable = false)
	private ContObject contObject;

	@Column(name = "cont_object_id")
	private Long contObjectId;

	@Version
	private int version;

	public ContGroup getContGroup() {
		return contGroup;
	}

	public void setContGroup(ContGroup contGroup) {
		this.contGroup = contGroup;
	}

	public ContObject getContObject() {
		return contObject;
	}

	public void setContObject(ContObject contObject) {
		this.contObject = contObject;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public void setContObjectId(Long contObjectId) {
		this.contObjectId = contObjectId;
	}

}
