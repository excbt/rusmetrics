package ru.excbt.datafuse.nmk.data.model.energypassport;

import ru.excbt.datafuse.nmk.data.model.EnergyPassportSectionTemplate;
import ru.excbt.datafuse.nmk.passdoc.PDTable;

/**
 * Created by kovtonyk on 31.03.2017.
 */
public interface EnergyPassportSectionTemplateFactory {
    EnergyPassportSectionTemplate createSectionTemplate();
    PDTable getPDTable();

    String createSectionTemplateJson(Boolean pretty);

    default String createSectionTemplateJson() {
        return createSectionTemplateJson(false);
    }

    String createValuesJson(boolean pretty);

    default String createValuesJson() {
        return createValuesJson(false);
    }
}
