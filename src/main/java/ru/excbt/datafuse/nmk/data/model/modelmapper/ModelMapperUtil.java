package ru.excbt.datafuse.nmk.data.model.modelmapper;

import org.modelmapper.ModelMapper;

/**
 * Created by kovtonyk on 11.04.2017.
 */
public class ModelMapperUtil {
    public final static ModelMapper MODEL_MAPPER = new ModelMapper();

    private ModelMapperUtil() {
    }

    public static <D> D map(Object source, Class<D> destinationType) {
        return MODEL_MAPPER.map(source,destinationType);
    }

}
