package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldMatrix;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class ApacheAdapter implements MatrixAdapter {

    private FieldMatrix<Complex> matrix;

    public ApacheAdapter(final FieldMatrix<Complex> matrix) {
        this.matrix = matrix;
    }

    @Override
    public double getReal(
            final int row,
            final int col) {
        return matrix.getEntry(row, col).getReal();
    }

    @Override
    public double getImaginary(
            final int row,
            final int col) {
        return matrix.getEntry(row, col).getImaginary();
    }

    @Override
    public void setReal(
            final int row,
            final int col,
            final double real) {
        final Complex entry = matrix.getEntry(row, col);
        matrix.setEntry(row, col, new Complex(real, entry.getImaginary()));
    }

    @Override
    public void setImaginary(
            final int row,
            final int col,
            final double imaginary) {
        final Complex entry = matrix.getEntry(row, col);
        matrix.setEntry(row, col, new Complex(entry.getReal(), imaginary));
    }

    @Override
    public void setComplex(
            final int row,
            final int col,
            final double real,
            final double imaginary) {
        matrix.setEntry(row, col, new Complex(real, imaginary));
    }
}
