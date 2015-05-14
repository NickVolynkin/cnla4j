package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldMatrix;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;

import java.util.Random;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class MatrixFactory {

    private static final int SPARSE_FACTOR = 4;

    private final int dimension;
    private final Density density;
    private final double[][] real;
    private final double[][] imaginary;


    private Complex[][] apacheDataCached;

    public MatrixFactory(
            final int dimension,
            final Density density) {

        this.dimension = dimension;
        this.density = density;
        real = new double[dimension][dimension];
        imaginary = new double[dimension][dimension];

        Random random = new Random();
        switch (density) {
            case DENSE:
            default:
                for (int row = 0; row < dimension; row++) {
                    for (int col = 0; col < dimension; col++) {
                        real[row][col] = random.nextDouble();
                        imaginary[row][col] = random.nextDouble();
                    }
                }
                break;
            case SPARSE:
                for (int row = 0; row < dimension; row++) {
                    int nonNull = random.nextInt(dimension);
                    real[row][nonNull] = random.nextDouble();
                    imaginary[row][nonNull] = random.nextDouble();
                    for (int col = 0; col < dimension; col++) {
                        if (col == nonNull) {
                            continue;
                        }
                        int dice = random.nextInt(SPARSE_FACTOR);
                        if (dice == 0) {
                            real[row][col] = random.nextDouble();
                            imaginary[row][col] = random.nextDouble();
                        } else {
                            real[row][col] = 0;
                            imaginary[row][col] = 0;
                        }
                    }
                }
                break;
            case IDENTITY:
                for (int row = 0; row < dimension; row++) {
                    for (int col = 0; col < dimension; col++) {
                        if (col == row) {
                            real[row][col] = 1;
                            imaginary[row][col] = 0;
                        } else {

                            real[row][col] = random.nextDouble();
                            imaginary[row][col] = random.nextDouble();
                        }
                    }
                }
        }
    }

    public FieldMatrix<Complex> getApacheDenseMatrix() {
        return new Array2DRowComplexMatrix(getApacheData());
    }

    public FieldMatrix<Complex> getApacheSparseMatrix() {
        return new SparseComplexMatrix(getApacheDenseMatrix());
    }

    public ComplexDoubleMatrix getJblasMatrix() {
        DoubleMatrix realMatix = new DoubleMatrix(real);
        DoubleMatrix imaginaryMatrix = new DoubleMatrix(imaginary);
        return new ComplexDoubleMatrix(realMatix, imaginaryMatrix);
    }


    private synchronized Complex[][] getApacheData() {
        if (apacheDataCached == null) {
            apacheDataCached = new Complex[dimension][dimension];

            for (int row = 0; row < dimension; row++) {
                for (int col = 0; col < dimension; col++) {
                    apacheDataCached[row][col] = new Complex(real[row][col], imaginary[row][col]);
                }
            }
        }
        return apacheDataCached;
    }


}
