package ru.excbt.datafuse.nmk.domain.datatype;

public class DoubleArrayTypeDescriptor extends AbstractArrayTypeDescriptor<double[]> {

    public static final DoubleArrayTypeDescriptor INSTANCE =
        new DoubleArrayTypeDescriptor();

    public DoubleArrayTypeDescriptor() {
        super(double[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "float";
    }
}
