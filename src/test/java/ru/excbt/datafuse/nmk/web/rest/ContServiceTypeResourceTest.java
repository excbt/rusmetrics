package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import static org.junit.Assert.*;

@Transactional
public class ContServiceTypeResourceTest extends AnyControllerTest {
    @Test
    public void getServiceTypes() throws Exception {
        _testGetJson("/api/contServiceTypes");
    }

}
