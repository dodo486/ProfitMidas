package bulls.tool.pricing;

import bulls.annotation.Todo;
import bulls.bs.CallPut;
import bulls.exception.NoBidAskDataException;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

import java.security.InvalidParameterException;

public class BSNew {
    //이산 배당이 있는 Stock의 옵션 가격 계산 방법은 여러가지(스팟에서 배당PV를 뺀 가격 사용, Strike을 배당에 따라 조정, Vol 조정 등)가 있지만
    //옵션의 만기가 길지 않으므로 간단하게 스팟에서 배당PV를 뺀 가격을 사용하는 방식(Hull, Option, Futures and Other Derivatives)을 사용함.
    //또한 코스피 옵션 가격의 경우 스팟 가격보단 선물 가격의 움직임에 따라 움직이므로 F=S*exp(rt) - D, S=(F+D)*exp(-rt)를 기존 BS formula에 대입해서 사용함.
    //결론 : BS Formula에서 S대신 F를 사용하고 q=r 값을 사용하면 선물 가격으로 부터 도출된 스팟 가격과 배당을 이용해서 평가하는 결과를 얻을 수 있음.
    //       또는 F를 현재가치로 할인한 값을 S로 사용하고 q=0으로 사용하는 것도 스팟에서 배당PV를 뺀 것과 비슷한 효과(S`=S-Dpv=(F+D)*exp(-rt)-Dpv=F*exp(-rt), 선물 만기와 배당일이 다를 경우 D*exp(-rt)와 Dpv가 동일하지는 않지만 무시)임
    //선물 가격 계산시 배당이 중요한 시기는 원지수 선물 만기 시점부터 배당락이 발생하는 해당월 말일까지이고 배당이 할인되는 기간은 대략 최대 20일 이내임.
    //분기 배당 0.64pt 가정시 20일 동안의 할인 여부에 따른 가격 차이는 0.0005 정도로 미미하고
    //연말 배당 4.6pt 가정시 20일 동안의 할인 여부에 따른 가격 차이는 0.001 정도로 마찬가지로 미미하므로 배당의 할인은 크게 고려하지 않아도 됨. (90일 할인의 경우 0.017pt 정도 차이)
    //참조 Carlos Veiga, Uwe Wystup, Closed Formula for Options with Discrete Dividends and its (Derivatives https://mathfinance.com/wp-content/uploads/2017/06/CPQF_Arbeits16.pdf)

    public static double tol = 1.48e-08;
    public static NormalDistribution nd = new NormalDistribution();

    public static double bs_formula(CallPut callPut, double S, double X, double T, double r, double q, double v) {
        //https://en.wikipedia.org/wiki/Black%E2%80%93Scholes_model#Black–Scholes_equation
        //Instruments paying continuous yield dividends
        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);

        double d2 = d1 - v * sqrtOfT;
        if (callPut == CallPut.CALL) {
            if (T <= 0)
                return FastMath.max(S - X, 0);
            else
                return FastMath.exp(-r * T) * (S * FastMath.exp((r - q) * T) * nd.cumulativeProbability(d1) - X * nd.cumulativeProbability(d2));
        } else {
            if (T <= 0)
                return FastMath.max(X - S, 0);
            else
                return FastMath.exp(-r * T) * (X * nd.cumulativeProbability(-d2) - S * FastMath.exp((r - q) * T) * nd.cumulativeProbability(-d1));
        }
    }


    @Todo(msg = " epsilon 루프에서 못 빠져나올때 어떻게 처리할 것인가...")
    public static double bs_impliedVol(CallPut callPut, double S, double X, double T, double r, double q, double cm) {


        double vi = FastMath.sqrt(FastMath.abs(FastMath.log(S / (X)) + (r - q) * T) * 2.0 / T);
        double ci = bs_formula(callPut, S, X, T, r, q, vi);
        double vegai = gVega(S, X, T, r, q, vi);
        double minDiff = FastMath.abs(cm - ci);
        double epsilon = cm * tol;

        int counter = 0;
        while (epsilon <= FastMath.abs(cm - ci) && FastMath.abs(cm - ci) <= minDiff) {
            vi -= (ci - cm) / vegai;
            ci = bs_formula(callPut, S, X, T, r, q, vi);
            vegai = gVega(S, X, T, r, q, vi);
            minDiff = FastMath.abs(cm - ci);
            counter++;
            if (counter == 200) {
                //DefaultLogger.logger.info("Tried more than 1000 times {} {} {} {} {}", callPut, S, X, T, cm);
                return Double.NaN;
            }
        }

        if (FastMath.abs(cm - ci) < epsilon) {
            return vi;
        } else {
            double bisectionVi = gBlackScholesImpVolBisection(callPut, S, X, T, r, q, cm);
            //DefaultLogger.logger.info("narrow down failed {} {} {} {} {},  vol from bisection: {}", callPut, S, X, T, cm, bisectionVi);
            return bisectionVi;
        }
    }


    public static double gDelta(CallPut callPut, double S, double X, double T, double r, double q, double v) {
        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);
        if (callPut == CallPut.CALL) {
            return FastMath.exp(-q * T) * nd.cumulativeProbability(d1);
        } else {
            return -FastMath.exp(-q * T) * nd.cumulativeProbability(-d1);
        }
    }

    public static double gGamma(double S, double X, double T, double r, double q, double v) {
        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);

        double gamma = FastMath.exp(-q * T) * nd.density(d1) / (S * v * sqrtOfT);
        return gamma;

    }

    public static double gTheta(CallPut callPut, double S, double X, double T, double r, double q, double v) {

        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);
        double d2 = d1 - v * sqrtOfT;
        double firstTerm = -(S * FastMath.exp(-q * T) * nd.density(d1) * v) / (2.0 * sqrtOfT);
        if (callPut == CallPut.CALL) {
            double secondTerm = q * S * FastMath.exp(-q * T) * nd.cumulativeProbability(d1) - r * X * FastMath.exp(-r * T) * nd.cumulativeProbability(d2);
            return firstTerm + secondTerm;
        } else if (callPut == CallPut.PUT) {
            double secondTerm = -q * S * FastMath.exp(-q * T) * nd.cumulativeProbability(-d1) + r * X * FastMath.exp(-r * T) * nd.cumulativeProbability(-d2);
            return firstTerm + secondTerm;
        } else
            throw new InvalidParameterException("callput flag should be 'p' or 'c'");
    }


    public static double gVega(double S, double X, double T, double r, double q, double v) {
        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);
        return S * FastMath.exp(-q * T) * nd.density(d1) * sqrtOfT;
    }

    public static double gRho(CallPut callPut, double S, double X, double T, double r, double q, double v) {

        double sqrtOfT = FastMath.sqrt(T);
        double d1 = (FastMath.log(S / X) + (r - q + v * v / 2.0) * T) / (v * sqrtOfT);
        double d2 = d1 - v * sqrtOfT;
        if (callPut == CallPut.CALL) {
            return X * T * FastMath.exp(-r * T) * nd.cumulativeProbability(d2);
        } else if (callPut == CallPut.PUT) {
            return -X * T * FastMath.exp(-r * T) * nd.cumulativeProbability(-d2);
        } else
            throw new InvalidParameterException("callput flag should be 'p' or 'c'");
    }

    public static double getPutPriceFromParity(int strike, double callPrice, double underLyingPrice, double r, double t) {
        //C-P=D(F-K)
        return callPrice + strike * FastMath.exp(-r * t) + underLyingPrice;
    }

    public static double getCallPriceFromParity(int strike, double putPrice, double underLyingPrice, double r, double t) {
        return underLyingPrice + putPrice - strike * FastMath.exp(-r * t);
    }

    public static double gBlackScholesImpVolBisection(CallPut CallPutFlag, double S,
                                                      double x, double T, double r, double b, double cm) {
        double vLow, vHigh, vi;
        double cLow, cHigh, epsilon;
        int counter;

        vLow = 0.005;
        vHigh = 4;
        epsilon = 0.0001;
        cLow = bs_formula(CallPutFlag, S, x, T, r, b, vLow);
        cHigh = bs_formula(CallPutFlag, S, x, T, r, b, vHigh);
        counter = 0;
        vi = vLow + (cm - cLow) * (vHigh - vLow) / (cHigh - cLow);

        while (Math.abs(cm - bs_formula(CallPutFlag, S, x, T, r, b, vi)) > epsilon) {
            counter = counter + 1;

            if (counter == 1000) {
                // N/A
//	        	System.out.println(cm + " " +  GBlackScholes(CallPutFlag, S, x, T, r, b, vi));
//	        	System.out.println(" Count exceed on impvol calculation :" +S+ " " + x + " " + T);
                return vi;
            }
            if (bs_formula(CallPutFlag, S, x, T, r, b, vi) < cm)
                vLow = vi;
            else
                vHigh = vi;

            cLow = bs_formula(CallPutFlag, S, x, T, r, b, vLow);
            cHigh = bs_formula(CallPutFlag, S, x, T, r, b, vHigh);
            vi = vLow + (cm - cLow) * (vHigh - vLow) / (cHigh - cLow);
        }

        return vi;
    }

    public static void main(String[] args) throws NoBidAskDataException {
        {
            //배당0.64pt를 연배당률로 환산해서 연속배당으로 반영
            CallPut callPut = CallPut.PUT;
            double S = 316.30, X = 315.0, T = 16 / 365.0, r = 00.0165, q = 0.0461, v = 0.149;
            double cm = 3.4942700689344566;
            double price = bs_formula(callPut, S, X, T, r, q, v);
            System.out.println(price);
            double delta = gDelta(callPut, S, X, T, r, q, v);
            System.out.println(delta);
            double gamma = gGamma(S, X, T, r, q, v);
            System.out.println(gamma);
            double vega = gVega(S, X, T, r, q, v);
            System.out.println(vega);
            double theta = gTheta(callPut, S, X, T, r, q, v);
            System.out.println(theta);
            double rho = gRho(callPut, S, X, T, r, q, v);
            System.out.println(rho);
            double vol = bs_impliedVol(callPut, S, X, T, r, q, cm);
            System.out.println(vol);
        }

        {
            //배당0.64pt를 S에서 바로 빼주고 q는 0으로 계산. 값 비슷하게 나옴.
            CallPut callPut = CallPut.PUT;
            double S = 316.30 - 0.64, X = 315.0, T = 16 / 365.0, r = 00.0165, q = 0, v = 0.149;
            double cm = 3.4942700689344566;
            double price = bs_formula(callPut, S, X, T, r, q, v);
            System.out.println(price);
            double delta = gDelta(callPut, S, X, T, r, q, v);
            System.out.println(delta);
            double gamma = gGamma(S, X, T, r, q, v);
            System.out.println(gamma);
            double vega = gVega(S, X, T, r, q, v);
            System.out.println(vega);
            double theta = gTheta(callPut, S, X, T, r, q, v);
            System.out.println(theta);
            double rho = gRho(callPut, S, X, T, r, q, v);
            System.out.println(rho);
            double vol = bs_impliedVol(callPut, S, X, T, r, q, cm);
            System.out.println(vol);
        }
//        {
//            double futT = 79/365.0;
//            double price = bsWithF(CallPut.PUT, 316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT);
//            System.out.println(price);
//            double delta = gDeltaWithF(CallPut.PUT, 316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT);
//            System.out.println(delta);
//            double gamma = gGammaWithF(316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT);
//            System.out.println(gamma);
//            double vega = gVegaWithF(316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT) / 100.0;
//            System.out.println(vega);
//            double theta = gThetaWithF(CallPut.PUT, 316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT) / 365.0;
//            System.out.println(theta);
//            double rho = gRhoWithF(CallPut.PUT, 316.34, 315.0, 16 / 365.0, 0.0165, 0.149,futT) / 100.0;
//            System.out.println(rho);
//            double vol = bsImpVolWithF(CallPut.PUT, 316.34, 315.0, 16 / 365.0, 0.0165, 8.51,futT);
//            System.out.println(vol);
//        }
//        System.out.println(price2 );
//        double vol = bs_impliedVol(CallPut.CALL, 22755, 23000.0, 0.00793,0.015,0.018, price );
//        System.out.println(vol );
//        price = bs_formula("C", 23340.0, 22750.0, 0.14614822163433275,0.018,0.015, vol);
//        System.out.println(price);
//        System.out.printf("price:" + price + " " + vega);
    }
}