package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.keyname.SessionDetailType;

public class ContZPointSessionDetailType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6596750848336568041L;

	public class ContZPointInfo {

		private final Long contZPointId;
		private final String customServiceName;
		private final String contServiceCaption;
		private final String contServiceName;
		private final String contServiceType;

		public ContZPointInfo(ContZPoint contZPoint) {
			this.contZPointId = contZPoint.getId();
			this.customServiceName = contZPoint.getCustomServiceName();
			this.contServiceCaption = contZPoint.getContServiceType().getCaption();
			this.contServiceName = contZPoint.getContServiceType().getName();
			this.contServiceType = contZPoint.getContServiceTypeKeyname();
		}

		public Long getContZPointId() {
			return contZPointId;
		}

		public String getCustomServiceName() {
			return customServiceName;
		}

		public String getContServiceName() {
			return contServiceName;
		}

		public String getContServiceCaption() {
			return contServiceCaption;
		}

		public String getContServiceType() {
			return contServiceType;
		}
	}

	@JsonUnwrapped
	private final ContZPointInfo contZPointInfo;

	private final List<SessionDetailType> sessionDetailTypes;

	public ContZPointSessionDetailType(ContZPoint contZPoint, List<SessionDetailType> sessionDetailTypes) {
		this.contZPointInfo = new ContZPointInfo(contZPoint);
		this.sessionDetailTypes = sessionDetailTypes;
	}

	public List<SessionDetailType> getSessionDetailTypes() {
		return sessionDetailTypes;
	}

	public ContZPointInfo getContZPointInfo() {
		return contZPointInfo;
	}

}
