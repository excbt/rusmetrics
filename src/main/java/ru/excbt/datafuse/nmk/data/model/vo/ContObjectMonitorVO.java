package ru.excbt.datafuse.nmk.data.model.vo;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.ModelWrapper;

public class ContObjectMonitorVO extends ModelWrapper<ContObject> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2212398265165383026L;

	/**
	 * 
	 * 
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 25.07.2016
	 *
	 */
	public class ContObjectStats {
		private int contZpointCount;

		private String contEventLevelColor;

		private long eventsCount;

		private long eventsTypesCount;

		public String getContEventLevelColor() {
			return contEventLevelColor;
		}

		public void setContEventLevelColor(String contEventLevelColor) {
			this.contEventLevelColor = contEventLevelColor;
		}

		public long getEventsCount() {
			return eventsCount;
		}

		public void setEventsCount(long eventsCount) {
			this.eventsCount = eventsCount;
		}

		public long getEventsTypesCount() {
			return eventsTypesCount;
		}

		public void setEventsTypesCount(long eventsTypesCount) {
			this.eventsTypesCount = eventsTypesCount;
		}

		public int getContZpointCount() {
			return contZpointCount;
		}

		public void setContZpointCount(int contZpointCount) {
			this.contZpointCount = contZpointCount;
		}
	}

	private final ContObjectStats contObjectStats;

	public ContObjectStats getContObjectStats() {
		return contObjectStats;
	}

	public long getNewEventsCount() {
		return newEventsCount;
	}

	public void setNewEventsCount(long newEventsCount) {
		this.newEventsCount = newEventsCount;
	}

	private long newEventsCount;

	public ContObjectMonitorVO(ContObject srcObject) {
		super(srcObject);
		this.contObjectStats = new ContObjectStats();
	}

}
