package ru.excbt.datafuse.nmk.data.model.keyname;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.DBMetadata;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "weather_provider")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter
public class WeatherProvider extends AbstractKeynameEntity implements DeletedMarker {

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

}
