package name.nickvolynkin.cnla4j;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class JblasAdapter implements MatrixAdapter {

    private final ComplexDoubleMatrix matrix;

    public JblasAdapter(final ComplexDoubleMatrix matrix) {
        this.matrix = matrix;
    }

    @Override
    public double getReal(
            final int row,
            final int col) {
        return matrix.get(row, col).real();
    }

    @Override
    public double getImaginary(
            final int row,
            final int col) {
        return matrix.get(row, col).imag();
    }

    @Override
    public void setReal(
            final int row,
            final int col,
            final double real) {
        final ComplexDouble value = matrix.get(row, col);
        value.set(real, value.imag());
    }

    @Override
    public void setImaginary(
            final int row,
            final int col,
            final double imaginary) {

        final ComplexDouble value = matrix.get(row, col);
        value.set(value.real(), imaginary);

    }

    @Override
    public void setComplex(
            final int row,
            final int col,
            final double real,
            final double imaginary) {

        final ComplexDouble value = matrix.get(row, col);
        value.set(real, imaginary);
    }


}
