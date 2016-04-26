package ru.excbt.datafuse.nmk.data.model.types;

public enum SubscrTypeKey implements AbstractKey {
	RMA, NORMAL, SUBSCR_CHILD(true), CABINET(true);

	private final boolean isChild;

	/**
	 * 
	 * @param isChild
	 */
	private SubscrTypeKey(boolean isChild) {
		this.isChild = isChild;
	}

	/**
	 * 
	 */
	private SubscrTypeKey() {
		this.isChild = false;
	}

	/**
	 * 
	 */
	@Override
	public String getKeyname() {
		return this.name().toUpperCase();
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	public static SubscrTypeKey searchKeyname(String keyname) {
		return AbstractKey.getEnumCodeFull(SubscrTypeKey.class, keyname);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isChild() {
		return isChild;
	}

}
