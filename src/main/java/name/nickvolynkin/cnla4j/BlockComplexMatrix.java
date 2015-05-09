package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.linear.BlockFieldMatrix;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class BlockComplexMatrix extends BlockFieldMatrix<Complex> {

    public BlockComplexMatrix(
            final int rows,
            final int columns) throws NotStrictlyPositiveException {
        super(ComplexField.getInstance(), rows, columns);
    }

    public BlockComplexMatrix(final Complex[][] rawData) throws DimensionMismatchException {
        super(rawData);
    }

    public BlockComplexMatrix(
            final int rows,
            final int columns,
            final Complex[][] blockData,
            final boolean copyArray) throws DimensionMismatchException, NotStrictlyPositiveException {
        super(rows, columns, blockData, copyArray);
    }
}
