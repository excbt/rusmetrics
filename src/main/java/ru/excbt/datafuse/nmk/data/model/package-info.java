@TypeDefs({
    @TypeDef(name = "JsonbAsString", typeClass = StringJsonUserType.class),
    @TypeDef(name = "TextArray", typeClass = StringArrayUserType.class),
    @TypeDef(name = "double-array",typeClass = DoubleArrayType.class
    )})
package ru.excbt.datafuse.nmk.data.model;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.excbt.datafuse.hibernate.types.StringArrayUserType;
import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
import ru.excbt.datafuse.nmk.domain.datatype.DoubleArrayType;
