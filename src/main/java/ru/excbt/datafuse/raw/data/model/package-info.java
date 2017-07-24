@TypeDefs({@TypeDef(name = "JsonbAsString", typeClass = StringJsonUserType.class),
    @TypeDef(name = "TextArray", typeClass = StringArrayUserType.class)})
package ru.excbt.datafuse.raw.data.model;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.excbt.datafuse.hibernate.types.StringArrayUserType;
import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
