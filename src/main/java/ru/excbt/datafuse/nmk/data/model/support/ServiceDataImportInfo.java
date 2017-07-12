/**
 *
 */
package ru.excbt.datafuse.nmk.data.model.support;

import lombok.Getter;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.12.2016
 *
 */
@Getter
public class ServiceDataImportInfo extends FileImportInfo {

	private final Long subscriberId;

	private final Long contObjectId;

	private final Long contZPointId;

	private final Long deviceObjectId;

	private final Long dataSourceId;

    //private final Long authorId;


    /**
     *
     * @param subscriberId
     * @param contObjectId
     * @param contZPointId
     * @param deviceObjectId
     * @param dataSourceId
     * @param userFileName
     * @param internalFileName
     */
    public ServiceDataImportInfo(Long subscriberId, Long contObjectId, Long contZPointId, Long deviceObjectId,
                                 Long dataSourceId, String userFileName, String internalFileName) {
        super(internalFileName, userFileName);
        this.subscriberId = subscriberId;
        this.contObjectId = contObjectId;
        this.contZPointId = contZPointId;
        this.deviceObjectId = deviceObjectId;
        this.dataSourceId = dataSourceId;
    }

}
