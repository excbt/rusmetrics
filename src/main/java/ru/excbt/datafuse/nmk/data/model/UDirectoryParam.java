package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractAuditableModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="u_directory_param")
@JsonIgnoreProperties (ignoreUnknown = true)
public class UDirectoryParam extends AbstractAuditableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3859570295447832844L;

	@Version
    @Column
    private int version;

    @ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "directory_id")	
    @JsonIgnore
	private UDirectory directory;    

    @Column(name="param_type")
    private String paramType;

    @Column(name="param_name")
    private String paramName;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public UDirectory getDirectory() {
		return directory;
	}

	public void setDirectory(UDirectory directory) {
		this.directory = directory;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
    
}
