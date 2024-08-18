package bulls.tool.pricing;

import bulls.annotation.Todo;
import bulls.bs.CallPut;
import bulls.exception.NoBidAskDataException;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

import java.security.InvalidParameterException;
public class BS {


    public static double tol = 1.48e-08;
    public static NormalDistribution nd = new NormalDistribution();

    public static double bs_formula(CallPut callPut, double S, double X, double T, double r, double b, double v) {
/*
//        (np.log(S / float(X)) + (b + v ** 2.0 / 2.0) * T) / (v * np.sqrt(T))
        double d1 = (FastMath.log( S/ (X*1.0) )  + ((b + v*v /2.0 ) * T ) ) / (v * FastMath.sqrt(T));
        double d2 = d1 - v * FastMath.sqrt(T);
//        double log = FastMath.log( S/ (X*1.0) );
//        double cdf = nd.cumulativeProbability(d1);
//        double tt = ((b + v*v /2.0 ) * T );
//        double cc = (v * FastMath.sqrt(T));
        if (callPut == CallPut.CALL) {
            if ( T == 0)
                return FastMath.max( S - X , 0);
            else
                return S * FastMath.exp((b-r) * T) * nd.cumulativeProbability(d1) - X * FastMath.exp(-r*T) * nd.cumulativeProbability(d2);
        } else {
            if ( T ==0 )
                return FastMath.max( X - S , 0);
            else
                return X * FastMath.exp ( -r * T ) * nd.cumulativeProbability(-d2) - S * FastMath.exp((b -r ) * T) * nd.cumulativeProbability(-d1);
        }*/

//        (np.log(S / float(X)) + (b + v ** 2.0 / 2.0) * T) / (v * np.sqrt(T))
        double d1 = (FastMath.log(S / (X)) + ((r - b + v * v / 2.0) * T)) / (v * FastMath.sqrt(T));
        double d2 = d1 - v * FastMath.sqrt(T);
//        double log = FastMath.log( S/ (X*1.0) );
//        double cdf = nd.cumulativeProbability(d1);
//        double tt = ((b + v*v /2.0 ) * T );
//        double cc = (v * FastMath.sqrt(T));
        if (callPut == CallPut.CALL) {
            if (T == 0)
                return FastMath.max(S - X, 0);
            else
                return FastMath.exp(-r * T) * (S * nd.cumulativeProbability(d1) - X * nd.cumulativeProbability(d2));
        } else {
            if (T == 0)
                return FastMath.max(X - S, 0);
            else
                return FastMath.exp(-r * T) * (X * nd.cumulativeProbability(-d2) - S * nd.cumulativeProbability(-d1));
        }
    }


    @Todo(msg = " epsilon 루프에서 못 빠져나올때 어떻게 처리할 것인가...")
    public static double bs_impliedVol(CallPut callPut, double S, double X, double T, double r, double b, double cm) {


        double vi = FastMath.sqrt(FastMath.abs(FastMath.log(S / (X)) + r * T) * 2.0 / T);
        double ci = bs_formula(callPut, S, X, T, r, b, vi);
        double vegai = gVega(S, X, T, r, b, vi);
        double minDiff = FastMath.abs(cm - ci);
        double epsilon = cm * tol;

        int counter = 0;
        while (epsilon <= FastMath.abs(cm - ci) && FastMath.abs(cm - ci) <= minDiff) {
            vi -= (ci - cm) / vegai;
            ci = bs_formula(callPut, S, X, T, r, b, vi);
            vegai = gVega(S, X, T, r, b, vi);
            minDiff = FastMath.abs(cm - ci);
            counter++;
            if (counter == 1000) {
//                DefaultLogger.logger.info("Tried more than 1000 times {} {} {} {} {}", callPut, S, X, T, cm);
                break;
            }
        }

        if (FastMath.abs(cm - ci) < epsilon) {
            return vi;
        } else {
            double bisectionVi = BlackScholesImpVol.GBlackScholesImpVolBisection(callPut, S, X, T, r, b, cm);
            //DefaultLogger.logger.info("narrow down failed {} {} {} {} {},  vol from bisection: {}", callPut, S, X, T, cm, bisectionVi);
            return bisectionVi;
        }
    }


    public static double gVega(double S, double X, double T, double r, double b, double v) {
        double d1 = (FastMath.log(S / (X)) + (b + v * v / 2.0) * T) / (v * FastMath.sqrt(T));
        return S * FastMath.exp((b - r) * T) * nd.density(d1) * FastMath.sqrt(T);
    }


    public static double GVega(double S, double x, double T, double b, double v) {
        double sqrtOfT = Math.sqrt(T);
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * sqrtOfT);


        double vega = nd.density(d1) * S * sqrtOfT / 100;

        return vega;

    }


    public static double gDelta(String callPutFlag, double S, double x, double T, double r, double b, double v) {
        double d1 = (FastMath.log(S / x) + (b + v * v / 2) * T) / (v * FastMath.sqrt(T));

        if (callPutFlag.equalsIgnoreCase("c")) {

            return FastMath.exp((b - r) * T) * nd.cumulativeProbability(d1);
        } else if (callPutFlag.equalsIgnoreCase("p"))
            return -FastMath.exp((b - r) * T) * nd.cumulativeProbability(-d1);

        else
            throw new InvalidParameterException("callput flag should be 'p' or 'c'");

    }

    public static double gDelta(CallPut callPutFlag, double S, double x, double T, double r, double b, double v) {
        double d1 = (FastMath.log(S / x) + (b + v * v / 2) * T) / (v * FastMath.sqrt(T));

        if (callPutFlag == CallPut.CALL) {

            return FastMath.exp((b - r) * T) * nd.cumulativeProbability(d1);
        } else if (callPutFlag == CallPut.PUT)
            return -FastMath.exp((b - r) * T) * nd.cumulativeProbability(-d1);

        else
            throw new InvalidParameterException("callput flag should be 'p' or 'c'");

    }


    public static double gTheta(CallPut callPut, double S, double x, double T, double r, double b, double v) {

        double SqrtOfT = FastMath.sqrt(T);
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * SqrtOfT);
        double d2 = d1 - v * SqrtOfT;


        double firstTerm = -(S * nd.density(d1) * v) / (2 * SqrtOfT);
        double secondTerm = r * x * FastMath.exp(-r * T) * nd.cumulativeProbability(d2);
        if (callPut == CallPut.CALL) {

            return firstTerm - secondTerm;
        } else if (callPut == CallPut.PUT)
            return firstTerm + secondTerm;

        else
            throw new InvalidParameterException("callput flag should be 'p' or 'c'");
    }

    public static double gGamma(double S, double x, double T, double b, double v) {
        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / x) + (b + v * v / 2) * T) / (v * sqrtOfT);

        double gamma = nd.density(d1) / (S * v * sqrtOfT);

        return gamma;

    }


    public static void main(String[] args) throws NoBidAskDataException {
        double price = bs_formula(CallPut.CALL, 22755, 23000.0, 0.00793, 0.015, 0.018, 0.21);
        double price2 = bs_formula(CallPut.CALL, 22755, 23000.0, 0.00793, 0.015, 0.018, 0.211);
//
//        double vega1 = gVega(247.45, 255.00, 28 / 365.0, 0.018, 0.015, 0.1);
//
//        double vega2 = BlackScholesImpVol.GVega(247.45, 255.00, 28 / 365.0, 0.015, 0.1);
//
//        System.out.println(vega1);
//        System.out.println(vega2);
        System.out.println(price);
        System.out.println(price2);
        double vol = bs_impliedVol(CallPut.CALL, 22755, 23000.0, 0.00793, 0.015, 0.018, price);
        System.out.println(vol);
//        price = bs_formula("C", 23340.0, 22750.0, 0.14614822163433275,0.018,0.015, vol);
//        System.out.println(price);
//        System.out.printf("price:" + price + " " + vega);
    }


    public static double getPutPriceFromParity(int strike, double callPrice, double underLyingPrice) {
        return callPrice + strike + underLyingPrice;
    }

    public static double getCallPriceFromParity(int strike, double putPrice, double underLyingPrice) {
        return underLyingPrice + putPrice - strike;
    }
}
