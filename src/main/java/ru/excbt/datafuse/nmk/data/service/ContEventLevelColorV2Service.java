package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventLevelColorV2;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContEventLevelColorV2Repository;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional(readOnly = true)
public class ContEventLevelColorV2Service {

    private final CopyOnWriteArrayList<ContEventLevelColorV2> colorsCache = new CopyOnWriteArrayList<>();

    private final ContEventLevelColorV2Repository contEventLevelColorV2Repository;

    public ContEventLevelColorV2Service(ContEventLevelColorV2Repository contEventLevelColorV2Repository) {
        this.contEventLevelColorV2Repository = contEventLevelColorV2Repository;
    }

    /**
     *
     * @param colorLevel
     * @return
     */
    public String getColorName (final Integer colorLevel) {
        if (colorsCache.isEmpty()) {
            colorsCache.addAll(contEventLevelColorV2Repository.findAll());
        }
        Optional<ContEventLevelColorV2> color = colorsCache.stream().filter(i -> colorLevel >= i.getLevelFrom() && colorLevel <= i.getLevelTo()).findFirst();
        return color.map(ContEventLevelColorV2::getKeyname).orElse(null);
    }



}

