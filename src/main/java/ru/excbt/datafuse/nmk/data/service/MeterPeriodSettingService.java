/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MeterPeriodSettingService {

	@Autowired
	private MeterPeriodSettingRepository meterPeriodSettingRepository;
	
	@Autowired
	protected ModelMapper modelMapper;

	/**
	 * 
	 * @param subscriberParam
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MeterPeriodSettingDTO> findBySubscriberId(SubscriberParam subscriberParam) {
		List<Long> ids = Lists.newArrayList(Long.valueOf(subscriberParam.getSubscriberId()), Long.valueOf(subscriberParam.getRmaSubscriberId()));
		
		List<MeterPeriodSetting> periodSettings = meterPeriodSettingRepository.findBySubscriberIds(ids);
		return periodSettings.stream().map(i -> modelMapper.map(i, MeterPeriodSettingDTO.class)).collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param meterPeriodSettingDTO
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public MeterPeriodSettingDTO save(MeterPeriodSettingDTO meterPeriodSettingDTO) {
		MeterPeriodSetting meterPeriodSetting = modelMapper.map(meterPeriodSettingDTO, MeterPeriodSetting.class);
		
		meterPeriodSettingRepository.save(meterPeriodSetting);
		
		return modelMapper.map(meterPeriodSetting, MeterPeriodSettingDTO.class);
		
	}
	
}
