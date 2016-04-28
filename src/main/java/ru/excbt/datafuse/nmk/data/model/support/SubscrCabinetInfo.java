package ru.excbt.datafuse.nmk.data.model.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public class SubscrCabinetInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4054689142438057427L;

	private final Subscriber subscriber;

	private final SubscrUser subscrUser;

	private final List<ContObjectShortInfo> contObjectInfoList;

	/**
	 * 
	 * @param subscriber
	 * @param subscrUser
	 * @param contObjects
	 */
	public SubscrCabinetInfo(Subscriber subscriber, SubscrUser subscrUser, List<ContObject> contObjects) {
		checkNotNull(contObjects);
		this.subscriber = subscriber;
		this.subscrUser = subscrUser;
		this.contObjectInfoList = Collections.unmodifiableList(
				contObjects.stream().map(i -> i.getContObjectShortInfo()).collect(Collectors.toList()));
	}

	/**
	 * 
	 * @param subscriber
	 * @param subscrUser
	 */
	public SubscrCabinetInfo(Subscriber subscriber, SubscrUser subscrUser) {
		this.subscriber = subscriber;
		this.subscrUser = subscrUser;
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
	public SubscrUser getSubscrUser() {
		return subscrUser;
	}

	/**
	 * 
	 * @return
	 */
	public List<ContObjectShortInfo> getContObjectInfoList() {
		return contObjectInfoList;
	}

}
