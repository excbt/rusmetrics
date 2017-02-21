/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api;

import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.service.MeterPeriodSettingService;
import ru.excbt.datafuse.nmk.web.api.support.ApiActionProcess;
import ru.excbt.datafuse.nmk.web.api.support.SubscrApiController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 * 
 */
@Controller
@RequestMapping(value = "/api/rma")
public class MeterPeriodSettingController extends SubscrApiController {

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

	/**
	 * 
	 * @param requestEntity
	 * @return
	 */
	@RequestMapping(value = "/meter-period-settings", method = RequestMethod.PUT,
			produces = APPLICATION_JSON_UTF8)
	public ResponseEntity<?> updateMeterPeriodSettingDTO(
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
	public ResponseEntity<?> createMeterPeriodSettingDTO(
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
	public ResponseEntity<?> deleteMeterPeriodSettingDTO(@PathVariable("id") Long id) {
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
	public ResponseEntity<?> getMeterPeriodSettingDTO(@PathVariable("id") Long id) {
		MeterPeriodSettingDTO result = meterPeriodSettingService.findOne(id);
		return result != null ? responseOK(result) : responseNotFound();
	}

}
