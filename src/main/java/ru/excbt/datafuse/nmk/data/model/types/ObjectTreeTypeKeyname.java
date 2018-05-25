package ru.excbt.datafuse.nmk.data.model.types;

import java.util.Optional;
import java.util.stream.Stream;

import ru.excbt.datafuse.nmk.data.model.markers.KeynameObject;

public enum ObjectTreeTypeKeyname implements KeynameObject {

	CONT_OBJECT_TREE_TYPE_1("contObjectTreeType1"),
    OBJECT_TREE_TYPE_TEMPLATE("objectTreeTypeTemplate");

	private final String urlName;

	private ObjectTreeTypeKeyname(String urlName) {
		this.urlName = urlName;

	}

	@Override
	public String getKeyname() {
		return this.name();
	}

	/**
	 *
	 * @param urlName
	 * @return
	 */
	public static ObjectTreeTypeKeyname findByUrl(String urlName) {

		Optional<ObjectTreeTypeKeyname> opt = Stream.of(ObjectTreeTypeKeyname.values())
				.filter((i) -> i.urlName.equalsIgnoreCase(urlName)).findFirst();

		return opt.isPresent() ? opt.get() : null;
	}

}
