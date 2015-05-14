package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.NonSquareMatrixException;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class ComplexLUDecomposition extends FieldLUDecomposition<Complex> {

    /**
     * Calculates the LU-decomposition of the given matrix.
     *
     * @param matrix
     *         The matrix to decompose.
     *
     * @throws NonSquareMatrixException
     *         if matrix is not square
     */
    public ComplexLUDecomposition(final FieldMatrix<Complex> matrix) {
        super(matrix);
    }
}
