package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;

import ru.excbt.datafuse.nmk.data.model.SessionDetailTypeContServiceType;

public class SessionDetailTypeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2322407982758097407L;

	private final String keyname;

	private final String caption;

	private final String description;

	private final String comment;

	/**
	 * 
	 * @param src
	 */
	public SessionDetailTypeInfo(SessionDetailTypeContServiceType src) {
		this.keyname = src.getSessionDetailType().getKeyname();
		this.caption = src.getCaption() != null ? src.getCaption() : src.getSessionDetailType().getCaption();
		this.description = src.getDescription() != null ? src.getDescription()
				: src.getSessionDetailType().getDescription();
		this.comment = src.getComment() != null ? src.getComment() : src.getSessionDetailType().getComment();
	}

	public String getKeyname() {
		return keyname;
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public String getComment() {
		return comment;
	}

}
