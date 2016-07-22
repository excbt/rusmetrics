package ru.excbt.datafuse.nmk.data.model.vo;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;

public class LogSessionVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 262339175623644138L;

	/**
	 * 
	 * 
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 02.06.2016
	 *
	 */
	@JsonInclude(value = Include.NON_NULL)
	public class DataSourceInfo {
		private final Long id;
		private final String keyname;
		private final String caption;
		private final String dataSourceName;

		public DataSourceInfo(SubscrDataSource subscrDataSource) {
			if (subscrDataSource == null) {
				this.id = null;
				this.keyname = null;
				this.caption = null;
				this.dataSourceName = null;
			} else {
				this.id = subscrDataSource.getId();
				this.keyname = subscrDataSource.getKeyname();
				this.caption = subscrDataSource.getCaption();
				this.dataSourceName = subscrDataSource.getDataSourceName();
			}
		}

		public Long getId() {
			return id;
		}

		public String getKeyname() {
			return keyname;
		}

		public String getCaption() {
			return caption;
		}

		public String getDataSourceName() {
			return dataSourceName;
		}
	}

	/**
	 * 
	 * 
	 * @author A.Kovtonyuk
	 * @version 1.0
	 * @since 02.05.2016
	 *
	 */
	@JsonInclude(value = Include.NON_NULL)
	public class DeviceObjectInfo {
		private final Long id;
		private final String number;
		private final Long deviceModelId;
		private final String deviceModelName;

		public DeviceObjectInfo(DeviceObject deviceObject) {
			if (deviceObject == null) {
				this.id = null;
				this.number = null;
				this.deviceModelId = null;
				this.deviceModelName = null;
			} else {
				this.id = deviceObject.getId();
				this.number = deviceObject.getNumber();
				this.deviceModelId = deviceObject.getDeviceModelId();
				this.deviceModelName = deviceObject.getDeviceModel().getModelName();
			}

		}

		public Long getId() {
			return id;
		}

		public String getNumber() {
			return number;
		}

		public Long getDeviceModelId() {
			return deviceModelId;
		}

		public String getDeviceModelName() {
			return deviceModelName;
		}
	}

	public class AuthorInfo {
		private final String authorId;
		private final String authorName;

		public AuthorInfo(LogSession logSession) {
			this.authorId = null;
			this.authorName = "Расписание";
		}

		public String getAuthorName() {
			return authorName;
		}

		public String getAuthorId() {
			return authorId;
		}
	}

	@JsonUnwrapped
	private final LogSession logSession;

	private final DataSourceInfo dataSourceInfo;

	private final DeviceObjectInfo deviceObjectInfo;

	private final AuthorInfo authorInfo;

	/**
	 * 
	 * @param logSession
	 */
	public LogSessionVO(final LogSession logSession) {
		checkNotNull(logSession);
		this.logSession = logSession;
		this.dataSourceInfo = new DataSourceInfo(logSession.getSubscrDataSource());
		this.deviceObjectInfo = new DeviceObjectInfo(logSession.getDeviceObject());
		this.authorInfo = new AuthorInfo(logSession);
	}

	/**
	 * 
	 * @return
	 */
	public LogSession getLogSession() {
		return logSession;
	}

	/**
	 * 
	 * @return
	 */
	public DataSourceInfo getDataSourceInfo() {
		return dataSourceInfo;
	}

	/**
	 * 
	 * @return
	 */
	public DeviceObjectInfo getDeviceObjectInfo() {
		return deviceObjectInfo;
	}

	public AuthorInfo getAuthorInfo() {
		return authorInfo;
	}
}
