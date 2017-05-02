package ru.excbt.datafuse.nmk.data.domain;

import java.io.Serializable;

/**
 * Created by kovtonyk on 02.05.2017.
 */
public interface PersistableBuilder <T extends PersistableBuilder<T,PK>, PK extends Serializable> {
    default T id(PK id) {
        setId(id);
        return (T) this;
    }

    void setId(final PK id);

}
