package name.nickvolynkin.cnla4j;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.MathArrays;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class CustomComplexLUDecomposition {

    public final Field<Complex> field = ComplexField.getInstance();

    /** Entries of both matrices of LU decomposition, stored in one to save memory */
    private Complex[][] lu; //stores both matrices in one to save memory.

    /** pivot vector, used instead of pivot matrix */
    private int[] pivot;

    /** Parity of permutation */
    private boolean even;

    /** Singularity indicator */
    private boolean singular;

    /** cached value of L */
    private FieldMatrix<Complex> cachedL;

    /** cached value of U */
    private FieldMatrix<Complex> cachedU;

    /** cached value of P */
    private FieldMatrix<Complex> cachedP;
    private Complex cachedDet;

    public CustomComplexLUDecomposition(FieldMatrix<Complex> matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }

        /** The matrix dimension */
        final int dimension = matrix.getColumnDimension();
        lu = matrix.getData();
        pivot = new int[dimension];
        cachedL = null;
        cachedP = null;
        cachedU = null;

        for (int row = 0; row < dimension; row++) {
            pivot[row] = row;
        }

        even = false;
        singular = true;

        for (int col = 0; col < dimension; col++) {
            Complex sum;
            final Complex zero = field.getZero();

            //upper matrix
            for (int row = 0; row < col; row++) {
                final Complex[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    //multithreading here
                    sum = sum.subtract(luRow[i].multiply(lu[i][col]));
                }
                luRow[col] = sum;
            }

            //lower matrix
            int nonZero = col; //permutation row
            for (int row = col; row < dimension; row++) {
                final Complex[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    //multithreading  here via Future<Complex>
                    final Complex factor = lu[i][col];
                    if (factor == null || factor.equals(zero)) {
                        continue;
                    }
                    final Complex product = luRow[i].multiply(factor);
                    //multithreading over
                    sum = sum.subtract(product);
                }
                luRow[col] = sum;

                if (lu[nonZero][col].equals(field.getZero())) {
                    //try to select a better permutation choice
                    ++nonZero;
                }
            }

            //singularity check
            if (nonZero >= dimension) {
                singular = true;
                return;
            }

            //pivot if necessary
            if (nonZero != col) {
                Complex tmp = field.getZero();
                for (int i = 0; i < dimension; i++) {
                    tmp = lu[nonZero][i];
                    lu[nonZero][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = pivot[nonZero];
                pivot[nonZero] = pivot[col];
                pivot[col] = pivot[temp];
                even = !even;
            }

            //divide the elements of lower rows by the "winning" diagonal element.
            final Complex luDiag = lu[col][col];
            for (int row = col + 1; row < dimension; row++) {
                final Complex[] luRow = lu[row];
                luRow[col] = luRow[col].divide(luDiag);
            }
        }
        //for(column) is over

    }

    public synchronized FieldMatrix<Complex> getL() {
        if ((cachedL == null) && !singular) {
            final int dimension = pivot.length;
            cachedL = getEmptyMatrix(dimension);
            for (int row = 0; row < dimension; ++row) {
                //multithread here
                final Complex[] luRow = lu[row];
                for (int col = 0; col < row; ++col) {
                    cachedL.setEntry(row, col, luRow[col]);
                }
                cachedL.setEntry(row, row, field.getOne());
            }
        }
        return cachedL;
    }

    public synchronized FieldMatrix<Complex> getU() {
        if ((cachedU == null) && !singular) {
            final int dimension = pivot.length;
            cachedU = getEmptyMatrix(dimension);
            for (int row = 0; row < dimension; ++row) {
                //multithread here
                final Complex[] luRow = lu[row];
                for (int col = row; col < dimension; ++col) {
                    cachedU.setEntry(row, col, luRow[col]);
                }
            }
        }
        return cachedU;
    }

    public synchronized FieldMatrix<Complex> getP() {
        if ((cachedP == null) && !singular) {
            final int dimension = pivot.length;
            cachedP = getEmptySparseMatrix(dimension);
            for (int row = 0; row < dimension; ++row) {
                cachedP.setEntry(row, pivot[row], field.getOne());
            }
        }
        return cachedP;
    }

    public int[] getPivot() {
        return pivot.clone();
    }

    public Complex getDeterminant() {
        if (singular) {
            return field.getZero();
        } else if (cachedDet == null) {
            final int dimension = pivot.length;
            //this part comes from L matrix.
            cachedDet = even ? field.getOne() : field.getZero().subtract(field.getOne());
            //this part comes from U matrix.
            for (int i = 0; i < dimension; i++) {
                cachedDet = cachedDet.multiply(lu[i][i]);
            }
        }

        return cachedDet;
    }

    private FieldMatrix<Complex> getEmptyMatrix(final int dimension) {
        return new Array2DRowComplexMatrix(dimension, dimension);
    }

    private SparseComplexMatrix getEmptySparseMatrix(final int dimension) {
        return new SparseComplexMatrix(dimension, dimension);
    }

    private static class Solver implements FieldDecompositionSolver<Complex> {

        private final Field<Complex> field = ComplexField.getInstance();

        private final Complex[][] lu;

        private final int[] pivot;
        private final boolean singular;

        private Solver(
                final Complex[][] lu,
                final int[] pivot,
                final boolean singular) {
            this.lu = lu;
            this.pivot = pivot;
            this.singular = singular;
        }

        /**
         * Check if the decomposed matrix is non-singular.
         *
         * @return true if the decomposed matrix is non-singular
         */
        @Override
        public boolean isNonSingular() {
            return !singular;
        }

        /**
         * Solve the linear equation A &times; X = B for matrices A.
         * <p>The A matrix is implicit, it is provided by the underlying
         * decomposition algorithm.</p>
         *
         * @param b
         *         right-hand side of the equation A &times; X = B
         *
         * @return a vector X that minimizes the two norm of A &times; X - B
         *
         * @throws DimensionMismatchException
         *         if the matrices dimensions do not match.
         * @throws SingularMatrixException
         *         if the decomposed matrix is singular.
         */
        @Override
        public FieldVector<Complex> solve(final FieldVector<Complex> b) {
            try {
                return solve((ArrayFieldVector<Complex>) b);
            } catch (ClassCastException cce) {
                final int dimension = pivot.length;
                final int length = b.getDimension();
                if (length != dimension) {
                    throw new DimensionMismatchException(length, dimension);
                }
                if (singular) {
                    throw new SingularMatrixException();
                }

                //apply permutations to b
                final Complex[] bp = MathArrays.buildArray(field, dimension);
                for (int row = 0; row < dimension; row++) {
                    bp[row] = b.getEntry(pivot[row]);
                }

                //solve LY = b by forward substitution
                for (int col = 0; col < dimension; col++) {
                    final Complex bpCol = bp[col];
                    for (int row = col + 1; row < dimension; row++) {
                        bp[row] = bp[row].subtract(bpCol.multiply(lu[row][col]));
                    }
                }

                //solve UX = Y by back substitution
                for (int col = dimension - 1; col >= 0; col--) {
                    bp[col] = bp[col].divide(lu[col][col]);
                    final Complex bpCol = bp[col];
                    for (int row = 0; row < col; row++) {
                        bp[row] = bp[row].subtract(bpCol.multiply(lu[row][col]));
                    }
                }
                return new ArrayFieldVector<>(field, bp, false);
            }
        }

        /**
         * http://mathworld.wolfram.com/LUDecomposition.html
         */
        public ArrayFieldVector<Complex> solve(ArrayFieldVector<Complex> b) {
            final int dimension = pivot.length;
            final int length = b.getDimension();
            if (length != dimension) {
                throw new DimensionMismatchException(length, dimension);
            }
            if (singular) {
                throw new SingularMatrixException();
            }
            //Apply permutations to b;
            //array of complex zeros;
            /**bp stands for b permuted */
            final Complex[] bp = MathArrays.buildArray(field, dimension);
            for (int row = 0; row < dimension; row++) {
                bp[row] = b.getEntry(pivot[row]);
            }

            //solve LY = b;
            for (int col = 0; col < dimension; col++) {
                final Complex bpCol = bp[col];
                for (int row = col + 1; row < dimension; row++) {
                    bp[row] = bp[row].subtract(bpCol.multiply(lu[row][col]));
                }
            }

            //solve UX = Y
            for (int col = dimension - 1; col >= 0; col--) {
                bp[col] = bp[col].divide(lu[col][col]);
                final Complex bpCol = bp[col];
                for (int row = 0; row < col; row++) {
                    bp[row] = bp[row].subtract(bpCol.multiply(lu[row][col]));
                }
            }
            return new ArrayFieldVector<>(field, bp, false);

        }

        /**
         * Solve the linear equation A &times; X = B for matrices A.
         * <p>The A matrix is implicit, it is provided by the underlying
         * decomposition algorithm.</p>
         *
         * @param b
         *         right-hand side of the equation A &times; X = B
         *
         * @return a matrix X that minimizes the two norm of A &times; X - B
         *
         * @throws DimensionMismatchException
         *         if the matrices dimensions do not match.
         * @throws SingularMatrixException
         *         if the decomposed matrix is singular.
         */
        @Override
        public FieldMatrix<Complex> solve(final FieldMatrix<Complex> b) {
            final int dimension = pivot.length;
            final int bRowDimension = b.getRowDimension();
            if (bRowDimension != dimension) {
                throw new DimensionMismatchException(bRowDimension, dimension);
            }
            if (singular) {
                throw new SingularMatrixException();
            }

            final int bColumnDimension = b.getColumnDimension();

            //apply permutations to B;
            final Complex[][] bp = MathArrays.buildArray(field, dimension, bColumnDimension);
            for (int row = 0; row < dimension; row++) {
                final Complex[] bpRow = bp[row];
                final int permutedRow = pivot[row];
                for (int col = 0; col < bColumnDimension; col++) {
                    bpRow[col] = b.getEntry(permutedRow, col);
                }
            }

            //solve LY = B;
            for (int col = 0; col < dimension; col++) {
                final Complex[] bpCol = bp[col];
                for (int i = col + 1; i < dimension; i++) {
                    final Complex[] bpI = bp[i];
                    final Complex luICol = lu[i][col];
                    for (int j = 0; j < bColumnDimension; j++) {
                        bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
                    }
                }
            }

            // Solve UX = Y
            for (int col = dimension - 1; col >= 0; col--) {
                final Complex[] bpCol = bp[col];
                final Complex luDiag = lu[col][col];
                for (int j = 0; j < bColumnDimension; j++) {
                    bpCol[j] = bpCol[j].divide(luDiag);
                }
                for (int i = 0; i < col; i++) {
                    final Complex[] bpI = bp[i];
                    final Complex luICol = lu[i][col];
                    for (int j = 0; j < bColumnDimension; j++) {
                        bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
                    }
                }
            }

            return new Array2DRowComplexMatrix(bp, false);
        }

        /**
         * Get the inverse (or pseudo-inverse) of the decomposed matrix.
         *
         * @return inverse matrix
         *
         * @throws SingularMatrixException
         *         if the decomposed matrix is singular.
         */
        @Override
        public FieldMatrix<Complex> getInverse() {
            final int m = pivot.length;
            final Complex one = field.getOne();
            FieldMatrix<Complex> identity = new Array2DRowFieldMatrix<>(field, m, m);
            for (int i = 0; i < m; ++i) {
                identity.setEntry(i, i, one);
            }

            return solve(identity);
        }
    }
}
