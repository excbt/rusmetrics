package ru.excbt.datafuse.nmk.data.service;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.ContObjectFiasRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by kovtonyk on 27.06.2017.
 */
@Service
public class ContObjectFiasService {

    private final ContObjectFiasRepository contObjectFiasRepository;
    private final FiasService fiasService;

    @Autowired
    public ContObjectFiasService(ContObjectFiasRepository contObjectFiasRepository, FiasService fiasService) {
        this.contObjectFiasRepository = contObjectFiasRepository;
        this.fiasService = fiasService;
    }

    /**
     *
     * @param contObjectFias
     */
    public void initCityUUID(ContObjectFias contObjectFias) {
        if (contObjectFias.getFiasUUID() != null) {
            UUID cityUUID = fiasService.getCityUUID(contObjectFias.getFiasUUID());
            if (cityUUID != null) {
                contObjectFias.setCityFiasUUID(cityUUID);
                String cityName = fiasService.getCityName(cityUUID);
                contObjectFias.setShortAddress2(cityName);

//                localPlaceService.saveCityToLocalPlace(cityUUID);
            }
            String shortAddr = fiasService.getShortAddr(contObjectFias.getFiasUUID());
            contObjectFias.setShortAddress1(shortAddr);
        } else {
            contObjectFias.setShortAddress1(null);
            contObjectFias.setShortAddress2(null);
        }
    }


    /**
     *
     * @param contObjectId
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public ContObjectFias findContObjectFias(Long contObjectId) {
        Preconditions.checkNotNull(contObjectId);
        List<ContObjectFias> vList = contObjectFiasRepository.findByContObjectId(contObjectId);
        return vList.isEmpty() ? null : vList.get(0);
    }


    /**
     *
     * @param contObject
     */
    public ContObjectFias createConfObjectFias(ContObject contObject) {
        ContObjectFias contObjectFias = new ContObjectFias();
        contObjectFias.setContObjectId(contObject.getId());
        contObjectFias.setFiasFullAddress(contObject.getFullAddress());
        contObjectFias.setGeoFullAddress(contObject.getFullAddress());
        contObjectFias.setIsGeoRefresh(true);
        return contObjectFias;
    }

    /**
     *
     * @param contObjectId
     * @param contObjectFias
     */
    @Transactional
    public void saveContObjectFias(final Long contObjectId, final ContObjectFias contObjectFias) {
        checkNotNull(contObjectId);
        contObjectFias.setContObjectId(contObjectId);
        contObjectFiasRepository.save(contObjectFias);

    }

    /**
     *
     * @param contObject
     */
    @Transactional
    public void contObjectFiasSetRefreshFlag(ContObject contObject) {
        checkArgument(!contObject.isNew());
        ContObjectFias contObjectFias =
            contObjectFiasRepository.findOneByContObjectId(contObject.getId()).map((i) -> {
                i.setIsGeoRefresh(true);
                return i;
            }).orElse(createConfObjectFias(contObject));
        contObjectFiasRepository.save(contObjectFias);
//		List<ContObjectFias> contObjectFiasList = contObjectFiasRepository.findByContObjectId(contObject.getId());
//		if (contObjectFiasList.size() == 0) {
//			contObjectFiasList.add(createConfObjectFias(contObject));
//		} else {
//			contObjectFiasList.forEach(i -> {
//				i.setIsGeoRefresh(true);
//			});
//		}
//		contObjectFiasRepository.save(contObjectFiasList);

    }


    /**
     *
     * @param contObjectIds
     * @return
     */
    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<ContObjectFias> selectContObjectsFias(List<Long> contObjectIds) {
        return contObjectIds == null || contObjectIds.isEmpty() ? new ArrayList<>()
            : contObjectFiasRepository.selectByContObjectIds(contObjectIds);
    }

    /**
     *
     * @param contObjectIds
     * @return
     */
    public Map<Long, ContObjectFias> selectContObjectsFiasMap(List<Long> contObjectIds) {
        return selectContObjectsFias(contObjectIds).stream()
            .collect(Collectors.toMap(ContObjectFias::getContObjectId, Function.identity()));

    }



}
