package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.SparseFieldMatrix;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class SparseComplexMatrix extends SparseFieldMatrix<Complex> {

//    public SparseComplexMatrix(final Field<Complex> field) {
    //        super(field);
    //    }

    public SparseComplexMatrix() {
        super(ComplexField.getInstance());
    }

    public SparseComplexMatrix(
            //            final Field<Complex> field,
            final int rowDimension,
            final int columnDimension) {
        super(ComplexField.getInstance(), rowDimension, columnDimension);
    }

    public SparseComplexMatrix(final SparseFieldMatrix<Complex> other) {
        super(other);
    }

    public SparseComplexMatrix(final FieldMatrix<Complex> other) {
        super(other);
    }
}
