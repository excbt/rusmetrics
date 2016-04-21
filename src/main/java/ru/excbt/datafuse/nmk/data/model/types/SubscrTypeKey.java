package ru.excbt.datafuse.nmk.data.model.types;

public enum SubscrTypeKey implements AbstractKey {
	RMA, NORMAL, SUBSCR_CHILD;

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

}
