package ru.excbt.datafuse.nmk.domain.datatype;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Properties;

public class DoubleArrayType extends AbstractSingleColumnStandardBasicType<double[]>
    implements DynamicParameterizedType {

    public DoubleArrayType() {
        super(ArraySqlTypeDescriptor.INSTANCE, DoubleArrayTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "double-array";
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((DoubleArrayTypeDescriptor)
            getJavaTypeDescriptor())
            .setParameterValues(parameters);
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}
