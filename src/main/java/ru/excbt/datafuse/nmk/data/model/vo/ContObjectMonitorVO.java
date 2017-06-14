package ru.excbt.datafuse.nmk.data.model.vo;

import lombok.Getter;
import lombok.Setter;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.ModelWrapper;

@Getter
@Setter
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
	@Getter
    @Setter
	public class ContObjectStats {
		private int contZpointCount;

		private String contEventLevelColor;

		private long eventsCount;

		private long eventsTypesCount;
	}

	private final ContObjectStats contObjectStats;

	private long newEventsCount;

	public ContObjectMonitorVO(ContObject srcObject) {
		super(srcObject);
		this.contObjectStats = new ContObjectStats();
	}

}
