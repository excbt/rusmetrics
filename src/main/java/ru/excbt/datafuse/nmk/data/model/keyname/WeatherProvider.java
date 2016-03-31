package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_provider")
public class WeatherProvider extends AbstractKeynameEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2818619676454494414L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "provider_name")
	private String providerName;

	@Column(name = "provider_description")
	private String providerDescription;

	@Column(name = "provider_comment")
	private String providerComment;

	@Column(name = "provider_url_template")
	private String providerUrlTemplate;

	@Column(name = "provider_response_type")
	private String providerResponseType;

	@Column(name = "provider_priority")
	private Integer providerPriority;

	@Column(name = "place_id_idx")
	private Integer placeIdIdx;

	@Column(name = "provider_place_id_name")
	private String providerPlaceIdName;

	@Column(name = "is_disabled")
	private Boolean isDisabled;

	@Version
	private int version;

	@Column(name = "deleted")
	private int deleted;

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderDescription() {
		return providerDescription;
	}

	public void setProviderDescription(String providerDescription) {
		this.providerDescription = providerDescription;
	}

	public String getProviderComment() {
		return providerComment;
	}

	public void setProviderComment(String providerComment) {
		this.providerComment = providerComment;
	}

	public String getProviderUrlTemplate() {
		return providerUrlTemplate;
	}

	public void setProviderUrlTemplate(String providerUrlTemplate) {
		this.providerUrlTemplate = providerUrlTemplate;
	}

	public String getProviderResponseType() {
		return providerResponseType;
	}

	public void setProviderResponseType(String providerResponseType) {
		this.providerResponseType = providerResponseType;
	}

	public Integer getProviderPriority() {
		return providerPriority;
	}

	public void setProviderPriority(Integer providerPriority) {
		this.providerPriority = providerPriority;
	}

	public Integer getPlaceIdIdx() {
		return placeIdIdx;
	}

	public void setPlaceIdIdx(Integer placeIdIdx) {
		this.placeIdIdx = placeIdIdx;
	}

	public String getProviderPlaceIdName() {
		return providerPlaceIdName;
	}

	public void setProviderPlaceIdName(String providerPlaceIdName) {
		this.providerPlaceIdName = providerPlaceIdName;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}