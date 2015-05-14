package name.nickvolynkin.cnla4j;

/**
 * @author Nick Volynkin  nick.volynkin@gmail.com
 */
public interface MatrixAdapter {

    public double getReal(
            int row,
            int col);

    public double getImaginary(
            int row,
            int col);

    public void setReal(
            int row,
            int col,
            double real);

    public void setImaginary(
            int row,
            int col,
            double imaginary);

    public void setComplex(
            int row,
            int col,
            double real,
            double imaginary);
}
