package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class Array2DRowComplexMatrix extends Array2DRowFieldMatrix<Complex> {

    public Array2DRowComplexMatrix() {
        super(ComplexField.getInstance());
    }

    public Array2DRowComplexMatrix(
            final int rowDimension,
            final int columnDimension) throws NotStrictlyPositiveException {
        super(ComplexField.getInstance(), rowDimension, columnDimension);
    }

    //    public Array2DRowComplexMatrix(final Complex[][] d) throws DimensionMismatchException,
    //                                                               NullArgumentException,
    //                                                               NoDataException {
    //        super(d);
    //    }

    public Array2DRowComplexMatrix(
            //            final Field<Complex> field,
            final Complex[][] d) throws DimensionMismatchException, NullArgumentException, NoDataException {
        super(ComplexField.getInstance(), d);
    }

    //    public Array2DRowComplexMatrix(
    //            final Complex[][] d,
    //            final boolean copyArray) throws DimensionMismatchException, NoDataException, NullArgumentException {
    //        super(d, copyArray);
    //    }

    public Array2DRowComplexMatrix(
            //            final Field<Complex> field,
            final Complex[][] d,
            final boolean copyArray) throws DimensionMismatchException, NoDataException, NullArgumentException {
        super(ComplexField.getInstance(), d, copyArray);
    }

    //    public Array2DRowComplexMatrix(final Complex[] v) throws NoDataException {
    //        super(v);
    //    }

    public Array2DRowComplexMatrix(
            //            final Field<Complex> field,
            final Complex[] v) {
        super(ComplexField.getInstance(), v);
    }
}
