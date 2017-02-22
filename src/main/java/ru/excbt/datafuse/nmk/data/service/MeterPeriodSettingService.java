/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.data.service.support.CurrentSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

/**
 * 
 * @author A.Kovtonyuk 
 * @version 1.0
 * @since 20.02.2017
 * 
 */
@Service
public class MeterPeriodSettingService extends AbstractService implements SecuredRoles {

	@Autowired
	private MeterPeriodSettingRepository meterPeriodSettingRepository;
	
	@Autowired
	protected ModelMapper modelMapper;

	@Autowired
	protected CurrentSubscriberService currentSubscriberService;
	
	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MeterPeriodSettingDTO> findBySubscriberId(SubscriberParam subscriberParam) {
		List<Long> ids = Lists.newArrayList(Long.valueOf(subscriberParam.getSubscriberId()), Long.valueOf(subscriberParam.getRmaSubscriberId()));
		
		List<MeterPeriodSetting> periodSettings = meterPeriodSettingRepository.findBySubscriberIds(ids);
		return periodSettings.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE).map(i -> modelMapper.map(i, MeterPeriodSettingDTO.class)).collect(Collectors.toList());
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public MeterPeriodSettingDTO findOne(Long id) {
		MeterPeriodSetting setting = meterPeriodSettingRepository.findOne(id);
		return setting != null && setting.getDeleted() == 0 ? modelMapper.map(setting, MeterPeriodSettingDTO.class) : null;
	}	
	
	/**
	 * 
	 * @param meterPeriodSettingDTO
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public MeterPeriodSettingDTO save(MeterPeriodSettingDTO meterPeriodSettingDTO) {
		
		if (meterPeriodSettingDTO.getId() != null) {
			MeterPeriodSetting check = meterPeriodSettingRepository.findOne(meterPeriodSettingDTO.getId());
			if (!check.getSubscriberId().equals(currentSubscriberService.getSubscriberId())) {
				throw new AccessDeniedException("Invalid subscriber Id");
			}
		}
		
		MeterPeriodSetting meterPeriodSetting = modelMapper.map(meterPeriodSettingDTO, MeterPeriodSetting.class);
		
		meterPeriodSetting.setSubscriberId(currentSubscriberService.getSubscriberId());
		meterPeriodSettingRepository.save(meterPeriodSetting);
		
		return modelMapper.map(meterPeriodSetting, MeterPeriodSettingDTO.class);
	}

	/**
	 * 
	 * @param id
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void delete(Long id) {
		MeterPeriodSetting setting = meterPeriodSettingRepository.findOne(id);
		if (setting != null) {
			setting.setDeleted(1);
			meterPeriodSettingRepository.save(setting);
		} else {
			entityNotFoundException (MeterPeriodSetting.class, id);
		}
	}
	
	
	
	
}
