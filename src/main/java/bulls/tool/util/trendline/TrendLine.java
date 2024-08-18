package bulls.tool.util.trendline;

// https://stackoverflow.com/questions/17592139/trend-lines-regression-curve-fitting-java-library
public interface TrendLine {
    void setValues(double[] y, double[] x); // y ~ f(x)

    double predict(double x); // get a predicted y for a given x

    double[][] getCoef();
}
