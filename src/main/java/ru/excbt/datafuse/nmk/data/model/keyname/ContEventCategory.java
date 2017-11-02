package ru.excbt.datafuse.nmk.data.model.keyname;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import ru.excbt.datafuse.nmk.data.domain.AbstractKeynameEntity;
import ru.excbt.datafuse.nmk.data.model.markers.DeletedMarker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "cont_event_category")
@JsonInclude(Include.NON_NULL)
public class ContEventCategory extends AbstractKeynameEntity implements DeletedMarker {

	/**
	 *
	 */
	private static final long serialVersionUID = 667344996165856933L;

	@Column(name = "caption")
	private String caption;

	@Column(name = "category_name")
	private String categoryName;

	@Column(name = "category_description")
	private String categoryDescription;

	@JsonIgnore
	@Column(name = "category_comment")
	private String categoryComment;

	@Column(name = "deleted")
	private int deleted;

	@Version
	private int version;

	@Column(name = "category_order")
	private Integer categoryOrder;

    public String getCaption() {
        return this.caption;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public String getCategoryDescription() {
        return this.categoryDescription;
    }

    public String getCategoryComment() {
        return this.categoryComment;
    }

    public int getDeleted() {
        return this.deleted;
    }

    public int getVersion() {
        return this.version;
    }

    public Integer getCategoryOrder() {
        return this.categoryOrder;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public void setCategoryComment(String categoryComment) {
        this.categoryComment = categoryComment;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setCategoryOrder(Integer categoryOrder) {
        this.categoryOrder = categoryOrder;
    }
}
