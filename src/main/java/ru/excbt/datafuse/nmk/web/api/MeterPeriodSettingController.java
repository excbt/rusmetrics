/**
 *
 */
package ru.excbt.datafuse.nmk.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.service.MeterPeriodSettingService;
import ru.excbt.datafuse.nmk.web.api.support.AbstractSubscrApiResource;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 *
 */
@Controller
@RequestMapping(value = "/api/subscr")
public class MeterPeriodSettingController extends AbstractSubscrApiResource {

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

	/**
	 *
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateMeterPeriodSetting(
			@Valid @RequestBody MeterPeriodSettingDTO meterPeriodSettingDTO) {

		if (meterPeriodSettingDTO.getId() == null) {
			return responseBadRequest();
		}

		ApiActionProcess<MeterPeriodSettingDTO> process = () -> {
			return meterPeriodSettingService.save(meterPeriodSettingDTO);
		};

		return WebApiHelper.processResponceApiActionUpdate(process);
	}

	/**
	 *
	 * @param meterPeriodSettingDTO
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> createMeterPeriodSetting(
			@Valid @RequestBody MeterPeriodSettingDTO meterPeriodSettingDTO, HttpServletRequest request) {

		if (meterPeriodSettingDTO.getId() != null) {
			return responseBadRequest();
		}

		ApiActionProcess<MeterPeriodSettingDTO> process = () -> {
			return meterPeriodSettingService.save(meterPeriodSettingDTO);
		};

		return responseCreate(process, () -> request.getRequestURI());
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings/{id}", method = RequestMethod.DELETE,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> deleteMeterPeriodSetting(@PathVariable("id") Long id) {
		return responseOK(() -> {
			meterPeriodSettingService.delete(id);
			return null;
		});
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings/{id}", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getMeterPeriodSetting(@PathVariable("id") Long id) {
		MeterPeriodSettingDTO result = meterPeriodSettingService.findOne(id);
		return result != null ? responseOK(result) : responseNotFound();
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings", method = RequestMethod.GET,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> getMeterPeriodSetting() {
		List<MeterPeriodSettingDTO> result = meterPeriodSettingService.findBySubscriberId(getSubscriberParam());
		return responseOK(result);
	}

}
