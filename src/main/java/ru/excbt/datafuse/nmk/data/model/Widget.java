/**
 *
 */
package ru.excbt.datafuse.nmk.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.excbt.datafuse.nmk.data.domain.AbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.filters.PropertyFilter;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;
import ru.excbt.datafuse.nmk.data.model.support.JsonModel;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.01.2017
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = DBMetadata.SCHEME_PORTAL, name = "_widget")
@JsonIgnoreProperties(value = { PropertyFilter.DEV_COMMENT_PROPERTY_IGNORE, "deleted" }, ignoreUnknown = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Widget extends AbstractPersistableEntity<Long> implements JsonModel, DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = 226386327909437693L;

	@Column(name = "widget_name")
	private String widgetName;

	@Column(name = "widget_path")
	private String widgetPath;

	@Column(name = "cont_service_type")
	private String contServiceType;

	@Column(name = "widget_params")
	@Type(type = "StringJsonObject")
	private String widgetParams;

	@Column(name = "caption")
	private String caption;

	@Column(name = "description")
	private String description;

	@Column(name = "dev_comment")
	private String devComment;

	@JsonIgnore
	@Version
	private int version;

	@JsonIgnore
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "order_idx")
	private Integer orderIdx;

	@Column(name = "is_default")
	private Boolean isDefault;

	/**
	 *
	 * @return
	 */
	public ContServiceTypeKey contServiceTypeKey() {
		return ContServiceTypeKey.searchKeyname(this.contServiceType);
	}

}
