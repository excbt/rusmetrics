package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "device_data_type")
public class DeviceDataType extends AbstractKeynameEntity {


	/**
	 *
	 */
	private static final long serialVersionUID = -3395634606730299938L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "data_type_name")
	private String dataTypeName;

	@Column(name = "data_type_comment")
	private String dataTypeComment;

	@Column(name = "ex_code")
	private String exCode;

	@Version
	private int version;

    @Column(name = "order_idx")
    @Getter
    @Setter
    private Integer orderIdx;



}
