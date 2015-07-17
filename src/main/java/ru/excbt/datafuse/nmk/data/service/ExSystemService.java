package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.repository.keyname.ExSystemRepository;

@Service
public class ExSystemService {

	public static final String PORTAL = "PORTAL";

	@Autowired
	private ExSystemRepository exSystemRepository;

}
