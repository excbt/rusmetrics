/**
 *
 */
package ru.excbt.datafuse.nmk.data.service;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.repository.MeterPeriodSettingRepository;
import ru.excbt.datafuse.nmk.security.AuthoritiesConstants;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import static ru.excbt.datafuse.nmk.security.AuthoritiesConstants.ADMIN;
import static ru.excbt.datafuse.nmk.security.AuthoritiesConstants.SUBSCR_ADMIN;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.02.2017
 *
 */
@Service
public class MeterPeriodSettingService {

	private final MeterPeriodSettingRepository meterPeriodSettingRepository;

	protected final ModelMapper modelMapper;

	private final PortalUserIdsService portalUserIdsService;

    public MeterPeriodSettingService(MeterPeriodSettingRepository meterPeriodSettingRepository, ModelMapper modelMapper, PortalUserIdsService portalUserIdsService) {
        this.meterPeriodSettingRepository = meterPeriodSettingRepository;
        this.modelMapper = modelMapper;
        this.portalUserIdsService = portalUserIdsService;
    }

    /**
     *
     * @param portalUserIds
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<MeterPeriodSettingDTO> findBySubscriberId(PortalUserIds portalUserIds) {
		List<Long> ids = Lists.newArrayList(portalUserIds.getSubscriberId(), portalUserIds.getRmaId()).stream()
            .filter(Objects::nonNull).collect(Collectors.toList());

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
	@Secured({SUBSCR_ADMIN, ADMIN })
	public MeterPeriodSettingDTO save(MeterPeriodSettingDTO meterPeriodSettingDTO) {

		if (meterPeriodSettingDTO.getId() != null) {
			MeterPeriodSetting check = meterPeriodSettingRepository.findOne(meterPeriodSettingDTO.getId());
			if (!check.getSubscriberId().equals(portalUserIdsService.getCurrentIds().getSubscriberId())) {
				throw new AccessDeniedException("Invalid subscriber Id");
			}
		}

		MeterPeriodSetting meterPeriodSetting = modelMapper.map(meterPeriodSettingDTO, MeterPeriodSetting.class);

		meterPeriodSetting.setSubscriberId(portalUserIdsService.getCurrentIds().getSubscriberId());
		meterPeriodSettingRepository.save(meterPeriodSetting);

		return modelMapper.map(meterPeriodSetting, MeterPeriodSettingDTO.class);
	}

	/**
	 *
	 * @param id
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ SUBSCR_ADMIN, ADMIN })
	public void delete(Long id) {
		MeterPeriodSetting setting = meterPeriodSettingRepository.findOne(id);
		if (setting != null) {
			setting.setDeleted(1);
			meterPeriodSettingRepository.save(setting);
		} else {
			DBExceptionUtil.entityNotFoundException (MeterPeriodSetting.class, id);
		}
	}

    public List<ContObject> filterMeterPeriodSettingIds(List<ContObject> contObjects, List<Long> meterPeriodSettingIds) {
        if (contObjects == null)
            return null;
        return contObjects.stream().filter(i -> {
            if (meterPeriodSettingIds == null)
                return true;
            List<Long> checkIds = i.getMeterPeriodSettings().values().stream().map(s -> s.getId()).collect(Collectors.toList());
            List<Long> filerM = new ArrayList<>(meterPeriodSettingIds);
            filerM.retainAll(checkIds);
            return !checkIds.isEmpty();
        }).collect(Collectors.toList());
    }



}
