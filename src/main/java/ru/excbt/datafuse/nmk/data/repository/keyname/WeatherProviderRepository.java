package ru.excbt.datafuse.nmk.data.repository.keyname;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.WeatherProvider;

public interface WeatherProviderRepository extends JpaRepository<WeatherProvider, String> {

}
