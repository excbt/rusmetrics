package ru.excbt.datafuse.nmk.data.model.markers;

public interface DeletableObject extends DeletedMarker {

	public void setDeleted(int deleted);
}
