package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;

@Deprecated
public class ContObjectWrapper {

	public class ContObjectStats {
		private int contZpointCount;

		public int getContZpointCount() {
			return contZpointCount;
		}

		public void setContZpointCount(int contZpointCount) {
			this.contZpointCount = contZpointCount;
		}
	}

	@JsonUnwrapped
	private final ContObject contObject;

	private final ContObjectStats contObjectStats;

	/**
	 * 
	 * @param contObject
	 */
	public ContObjectWrapper(final ContObject contObject) {
		this.contObject = contObject;
		this.contObjectStats = new ContObjectStats();
	}

	/**
	 * 
	 * @return
	 */
	public ContObjectStats getContObjectStats() {
		return contObjectStats;
	}

	/**
	 * 
	 * @return
	 */
	public ContObject getContObject() {
		return contObject;
	}

	/**
	 * 
	 * @param contObject
	 * @return
	 */
	@Deprecated
	public static ContObjectWrapper wrapContObject(ContObject contObject) {
		checkNotNull(contObject);
		return new ContObjectWrapper(contObject);
	}

	/**
	 * 
	 * @param contObjects
	 * @return
	 */
	@Deprecated
	public static List<ContObjectWrapper> wrapContObjects(List<ContObject> contObjects) {
		checkNotNull(contObjects);
		return wrapContObjects(contObjects, true);
	}

	/**
	 * 
	 * @param contObjects
	 * @param deletedFilter
	 * @return
	 */
	@Deprecated
	public static List<ContObjectWrapper> wrapContObjects(List<ContObject> contObjects, boolean deletedFilter) {
		checkNotNull(contObjects);

		if (deletedFilter) {
			return contObjects.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
					.map(i -> new ContObjectWrapper(i)).collect(Collectors.toList());
		}

		return contObjects.stream().map(i -> new ContObjectWrapper(i)).collect(Collectors.toList());
	}

}
