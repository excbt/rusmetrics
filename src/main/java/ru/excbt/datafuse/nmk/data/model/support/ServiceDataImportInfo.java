/**
 * 
 */
package ru.excbt.datafuse.nmk.data.model.support;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.12.2016
 * 
 */
public class ServiceDataImportInfo {

	private final Long subscriberId;

	private final Long contObjectId;

	private final Long contZPointId;

	private final String timeDetailType;

	private final String fileName;

	/**
	 * @param subscriberId
	 * @param contObjectId
	 * @param contZPointId
	 * @param timeDetailType
	 * @param fileName
	 */
	public ServiceDataImportInfo(Long subscriberId, Long contObjectId, Long contZPointId, String timeDetailType,
			String fileName) {
		this.subscriberId = subscriberId;
		this.contObjectId = contObjectId;
		this.contZPointId = contZPointId;
		this.timeDetailType = timeDetailType;
		this.fileName = fileName;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public Long getContObjectId() {
		return contObjectId;
	}

	public Long getContZPointId() {
		return contZPointId;
	}

	public String getTimeDetailType() {
		return timeDetailType;
	}

	public String getFileName() {
		return fileName;
	}

}
