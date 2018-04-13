package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Transactional
public class RmaSubscriberResourceTest extends RmaControllerTest {


	@Autowired
	private SubscriberService subscriberService;

    @Autowired
    private RmaSubscriberService rmaSubscriberService;

    @Autowired
    private OrganizationService organizationService;

    private Organization createOrganization () {
        Organization org = new Organization();
        org.setOrganizationName("test org");
        return organizationService.saveOrganization(org);
    }

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testGetSubscribers() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl("/subscribers"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testGetSubscriber() throws Exception {

        Organization org = createOrganization();

        assertNotNull(org.getId());
	    SubscriberDTO dto = SubscriberDTO.builder().subscriberName("Test Subscriber").organizationId(org.getId()).timezoneDefKeyname("MSK").build();
	    Subscriber subscriber = rmaSubscriberService.createRmaSubscriber(dto, getSubscriberId());

	    assertNotNull(subscriber.getId());
		_testGetJson("/api/rma/subscribers/" + subscriber.getId());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testSubcriberCRUD() throws Exception {

        Organization org = createOrganization();


        SubscriberDTO dto = SubscriberDTO.builder()
            .subscriberName("TEST_SUBSCRIBER")
            .organizationId(org.getId())
            .timezoneDefKeyname(TimezoneDefKey.GMT_M3.getKeyname()).build();

		Long subscriberId = _testCreateJson(UrlUtils.apiRmaUrl("/subscribers"), dto);

		SubscriberDTO createdDTO = subscriberService.findSubscriberDTO(subscriberId).map(s -> {
            s.setComment("Updated By REST");
            s.setCanCreateChild(true);
            return s;
        }).orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Subscriber.class, subscriberId));

		_testUpdateJson("/api/rma/subscribers/" + subscriberId, createdDTO);

		_testGetJson("/api/rma/subscribers/" + subscriberId);

		_testDeleteJson("/api/rma/subscribers/" + subscriberId,
            b -> b.param("isPermanent", "false"));
	}
}
