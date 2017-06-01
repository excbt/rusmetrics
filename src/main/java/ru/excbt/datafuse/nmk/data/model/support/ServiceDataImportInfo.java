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

	private final Long deviceObjectId;

	private final Long dataSourceId;

	private final Long authorId;

	private final String internalFileName;

	private final String userFileName;

    /**
     *
     * @param subscriberId
     * @param contObjectId
     * @param contZPointId
     * @param deviceObjectId
     * @param dataSourceId
     * @param authorId
     * @param userFileName
     * @param internalFileName
     */
	public ServiceDataImportInfo(Long subscriberId, Long contObjectId, Long contZPointId, Long deviceObjectId,
			Long dataSourceId, Long authorId, String userFileName, String internalFileName) {
		super();
		this.subscriberId = subscriberId;
		this.contObjectId = contObjectId;
		this.contZPointId = contZPointId;
		this.deviceObjectId = deviceObjectId;
		this.dataSourceId = dataSourceId;
		this.authorId = authorId;
		this.userFileName = userFileName;
		this.internalFileName = internalFileName;
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

	public String getInternalFileName() {
		return internalFileName;
	}

	public Long getDeviceObjectId() {
		return deviceObjectId;
	}

	public Long getDataSourceId() {
		return dataSourceId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public String getUserFileName() {
		return userFileName;
	}

}
