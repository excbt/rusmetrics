package ru.excbt.datafuse.nmk.data.model.support;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;

@Getter
public class ContZPointSessionDetailType implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6596750848336568041L;

	@Getter
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

	}

	@JsonUnwrapped
	private final ContZPointInfo contZPointInfo;

	private final List<SessionDetailTypeInfo> sessionDetailTypes;

	public ContZPointSessionDetailType(ContZPoint contZPoint, List<SessionDetailTypeInfo> sessionDetailTypes) {
		this.contZPointInfo = new ContZPointInfo(contZPoint);
		this.sessionDetailTypes = sessionDetailTypes;
	}

}
