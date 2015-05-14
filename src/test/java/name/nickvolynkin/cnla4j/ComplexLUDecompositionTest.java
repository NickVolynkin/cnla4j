package name.nickvolynkin.cnla4j;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.FieldMatrix;
import org.jblas.ComplexDoubleMatrix;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.*;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public class ComplexLUDecompositionTest {

    private MatrixFactory matrixFactory;

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
    private FieldMatrix<Complex> apacheMatrix;
    private ComplexDoubleMatrix jblasMatrix;
    private ComplexLUDecomposition complexLUDecomposition;

    @Before
    public void prepare(){
        matrixFactory = new MatrixFactory(10, Density.SPARSE);
        apacheMatrix = matrixFactory.getApacheSparseMatrix();
        jblasMatrix = matrixFactory.getJblasMatrix();


    }

    @Test()
    public void getApacheDecomposition() {
        complexLUDecomposition = new ComplexLUDecomposition(apacheMatrix);
    }

    @Test
    public void getApacheLUP() {

    }

    @Test()
    public void getApacheDeterminant(){
        final Complex determinant = complexLUDecomposition.getDeterminant();
    }



}