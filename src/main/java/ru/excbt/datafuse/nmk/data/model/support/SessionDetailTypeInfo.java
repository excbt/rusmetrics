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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyname == null) ? 0 : keyname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionDetailTypeInfo other = (SessionDetailTypeInfo) obj;
		if (keyname == null) {
			if (other.keyname != null)
				return false;
		} else if (!keyname.equals(other.keyname))
			return false;
		return true;
	}

}
