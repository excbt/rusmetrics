package ru.excbt.datafuse.nmk.data.model.energypassport;


/**
 * Created by kovtonyk on 31.03.2017.
 */
public interface EnergyPassportSectionTemplateFactory {

    String createSectionTemplateJson(Boolean pretty);

    default String createSectionTemplateJson() {
        return createSectionTemplateJson(false);
    }

    String createValuesJson(boolean pretty);

    default String createValuesJson() {
        return createValuesJson(false);
    }

}
