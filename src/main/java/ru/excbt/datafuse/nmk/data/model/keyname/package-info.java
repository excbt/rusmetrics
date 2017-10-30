/**
 * Created by kovtonyk on 09.06.2017.
 */
@TypeDefs({@TypeDef(name = "JsonbAsString", typeClass = StringJsonUserType.class),
    @TypeDef(name = "TextArray", typeClass = StringArrayUserType.class)})
package ru.excbt.datafuse.nmk.data.model.keyname;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import ru.excbt.datafuse.hibernate.types.StringArrayUserType;
import ru.excbt.datafuse.hibernate.types.StringJsonUserType;
