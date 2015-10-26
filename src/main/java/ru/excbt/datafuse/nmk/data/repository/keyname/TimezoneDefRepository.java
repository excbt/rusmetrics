package ru.excbt.datafuse.nmk.data.repository.keyname;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;

public interface TimezoneDefRepository extends JpaRepository<TimezoneDef, String> {

	public List<TimezoneDef> findByIsDefault(Boolean isDeafult);

}
