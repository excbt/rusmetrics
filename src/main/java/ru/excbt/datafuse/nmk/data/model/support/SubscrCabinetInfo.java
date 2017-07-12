package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public class SubscrCabinetInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 4054689142438057427L;

	@JsonUnwrapped()
	private final Subscriber subscriber;

	@JsonProperty(value = "subscrUser")
	private final SubscrUserWrapper subscrUserWrapper;

	@JsonProperty(access = Access.READ_ONLY)
	private final List<ContObjectShortInfo> contObjectInfoList;

    /**
     *
     * @param subscriber
     * @param subscrUser
     * @param contObjectShortInfos
     */
	public SubscrCabinetInfo(Subscriber subscriber, SubscrUser subscrUser,
			List<ContObjectShortInfo> contObjectShortInfos) {
		checkNotNull(contObjectShortInfos);
		this.subscriber = subscriber;
		this.subscrUserWrapper = new SubscrUserWrapper(subscrUser);
		this.contObjectInfoList = Collections.unmodifiableList(contObjectShortInfos);
	}

	/**
	 *
	 * @param subscriber
	 * @param subscrUser
	 */
	public SubscrCabinetInfo(Subscriber subscriber, SubscrUser subscrUser) {
		this.subscriber = subscriber;
		this.subscrUserWrapper = new SubscrUserWrapper(subscrUser);
		this.contObjectInfoList = Collections.unmodifiableList(new ArrayList<>());
	}

	/**
	 *
	 * @return
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 *
	 * @return
	 */
	public List<ContObjectShortInfo> getContObjectInfoList() {
		return contObjectInfoList;
	}

	public SubscrUserWrapper getSubscrUserWrapper() {
		return subscrUserWrapper;
	}

}
