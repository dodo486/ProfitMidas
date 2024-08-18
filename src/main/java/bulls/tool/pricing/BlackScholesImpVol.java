package bulls.tool.pricing;

import bulls.bs.CallPut;
import bulls.exception.NoBidAskDataException;
import bulls.exception.UnidentifiedStockCodeException;
import bulls.log.DefaultLogger;
import bulls.staticData.ExpiryCenter;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.staticData.TimeTillExpiryCenter;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BlackScholesImpVol {

    public static double RFR = 0.015;
    public static double CARRY = 0.018;

    private static final double TOTAL_HOURS = 252 * 6;


    public static NormalDistribution nd = new NormalDistribution();

    public static double getImpVol(String code, double underlyingPrice, double optionPrice, long time) throws UnidentifiedStockCodeException {
        CallPut putCallId = getCallPutId(code);
        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '2' || strikePriceString.charAt(2) == '7') {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));

        Double FuturePrice = underlyingPrice;

        double v = BS.bs_impliedVol(putCallId, FuturePrice, strikePrice * 100, timeToMaturity, RFR, CARRY, optionPrice);
        return v;
    }


    public static CallPut getCallPutId(String code) throws UnidentifiedStockCodeException {
        ProdType pType = ProdTypeCenter.Instance.getProdType(code);
        CallPut callPut;
        if (pType.isCallOption())
            callPut = CallPut.CALL;
        else if (pType.isPutOption())
            callPut = CallPut.PUT;
        else {
            String msg = "Black-scholes Calculation is only for Options, check Isin Code :" + code;
            throw new UnidentifiedStockCodeException(code, msg);
        }

        return callPut;
    }

    public static double getImpVolByMinuteMaturity(String code, long time, double marketPriceOption, double futurePrice) throws UnidentifiedStockCodeException {
        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '2' || strikePriceString.charAt(2) == '7') {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));


//		double v = GBlackScholesImpVolBisection(putCallId, futurePrice, strikePrice * 100, timeToMaturity, RFR, CARRY, makretPriceOfOption);
        double v = BS.bs_impliedVol(putCallId, futurePrice, strikePrice * 100, timeToMaturity, RFR, CARRY, marketPriceOption);
//		if ( isinCode.indexOf("KR4201FB2653") >= 0 )
//			System.out.println(isinCode.substring(8,11) + " " + makretPriceOfOption + " " + v + " " + timeToMaturity);

        return v;
    }

    // S - StockPrice, x - StrikePrice, T - TimeToMaturity, r - ccRate, b - ccCarry, cm - market price of option
    public static double GBlackScholesImpVolBisection(CallPut CallPutFlag, double S,
                                                      double x, double T, double r, double b, double cm) {
        double vLow, vHigh, vi;
        double cLow, cHigh, epsilon;
        int counter;

        vLow = 0.005;
        vHigh = 4;
        epsilon = 0.0001;
        cLow = BS.bs_formula(CallPutFlag, S, x, T, r, b, vLow);
        cHigh = BS.bs_formula(CallPutFlag, S, x, T, r, b, vHigh);
        counter = 0;
        vi = vLow + (cm - cLow) * (vHigh - vLow) / (cHigh - cLow);

        while (Math.abs(cm - BS.bs_formula(CallPutFlag, S, x, T, r, b, vi)) > epsilon) {
            counter = counter + 1;

            if (counter == 1000) {
                // N/A
//	        	System.out.println(cm + " " +  GBlackScholes(CallPutFlag, S, x, T, r, b, vi));
//	        	System.out.println(" Count exceed on impvol calculation :" +S+ " " + x + " " + T);
                return vi;
            }
            if (BS.bs_formula(CallPutFlag, S, x, T, r, b, vi) < cm)
                vLow = vi;
            else
                vHigh = vi;

            cLow = BS.bs_formula(CallPutFlag, S, x, T, r, b, vLow);
            cHigh = BS.bs_formula(CallPutFlag, S, x, T, r, b, vHigh);
            vi = vLow + (cm - cLow) * (vHigh - vLow) / (cHigh - cLow);
        }

        return vi;
    }


    public static double GDelta(CallPut callPut, double S, double x, double T, double r, double b, double v) {
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * Math.sqrt(T));

        if (callPut == CallPut.CALL)
            return Math.exp((b - r) * T) * nd.cumulativeProbability(d1);
        else
            return -Math.exp((b - r) * T) * nd.cumulativeProbability(-d1);

    }

    public static double GGamma(double S, double x, double T, double b, double v) {
        double sqrtOfT = Math.sqrt(T);
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * sqrtOfT);


        double gamma = nd.density(d1) / (S * v * sqrtOfT);

        return gamma;

    }

    public static double GVega(double S, double x, double T, double b, double v) {
        double sqrtOfT = Math.sqrt(T);
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * sqrtOfT);


        double vega = nd.density(d1) * S * sqrtOfT / 100;

        return vega;

    }

    public static double GTheta(CallPut callPut, double S, double x, double T, double r, double b, double v) {

        double SqrtOfT = Math.sqrt(T);
        double d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * SqrtOfT);
        double d2 = d1 - v * SqrtOfT;


        double firstTerm = -(S * nd.density(d1) * v) / (2 * SqrtOfT);
        double secondTerm = r * x * Math.exp(-r * T) * nd.cumulativeProbability(d2);
        if (callPut == CallPut.CALL) {

            return firstTerm - secondTerm;
        } else
            return firstTerm + secondTerm;

    }

//	public static double GBlackScholes(String callPutFlag, double S, double x,
//			double T, double r, double b, double v) throws InvalidParameterException
//	{
//		double d1, d2;
//
//		d1 = (Math.log(S / x) + (b + v * v / 2) * T) / (v * Math.sqrt(T));
//		d2 = d1 - v * Math.sqrt(T);
//
//		if (callPutFlag.equalsIgnoreCase("c")) {
//
////			System.out.println("d1 = " + d1);
//
//			return S * Math.exp( (b - r) * T ) * nd.cumulativeProbability(d1) - x * Math.exp(-r * T) * nd.cumulativeProbability(d2);
//		}
//		else if (callPutFlag.equalsIgnoreCase("p"))
//			return x * Math.exp( -r * T) * nd.cumulativeProbability(-d2) - S * Math.exp((b - r) * T) * nd.cumulativeProbability(-d1);
//
//		else
//			throw new InvalidParameterException("callput flag should be 'p' or 'c'");
//
//	}

    // Cummulative double precision algorithm based on Hart 1968
    // Based on implementation by Graeme West
    public static double CND(double x) {
        double y;
        double Exponential;
        double SumA;
        double SumB;

        double r;

        y = Math.abs(x);

        if (y > 37)
            return 0;
        else {
            Exponential = Math.exp(-1 * (y * y / 2));

            if (y < 7.07106781186547) {
                SumA = 3.52624965998911E-02 * y + 0.700383064443688;
                SumA = SumA * y + 6.37396220353165;
                SumA = SumA * y + 33.912866078383;
                SumA = SumA * y + 112.079291497871;
                SumA = SumA * y + 221.213596169931;
                SumA = SumA * y + 220.206867912376;
                SumB = 8.83883476483184E-02 * y + 1.75566716318264;
                SumB = SumB * y + 16.064177579207;
                SumB = SumB * y + 86.7807322029461;
                SumB = SumB * y + 296.564248779674;
                SumB = SumB * y + 637.333633378831;
                SumB = SumB * y + 793.826512519948;
                SumB = SumB * y + 440.413735824752;
                r = Exponential * SumA / SumB;
            } else {
                SumA = y + 0.65;
                SumA = y + 4 / SumA;
                SumA = y + 3 / SumA;
                SumA = y + 2 / SumA;
                SumA = y + 1 / SumA;
                r = Exponential / (SumA * 2.506628274631);
            }
        }

        if (x > 0)
            r = 1 - r;

        return r;
    }

    public static double derivativeCND(double x) {

        double pi = 3.141592;

        return (1.0 / Math.sqrt(2 * pi)) * Math.exp(-(x * x) / 2.0);

    }

    // 2°�� ����� ����
    public static boolean isMaturity(Calendar oCalendar) {
        if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            return false;

        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        return (day - 7) >= 1 && (day - 7) <= 7;
    }

    // expireMonth ���
    public static boolean isMaturity(Calendar oCalendar, int expireMonth) {
        if ((expireMonth > -1) && (oCalendar.get(Calendar.MONTH) != expireMonth - 1))
            return false;

        if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            return false;

        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        return (day - 7) >= 1 && (day - 7) <= 7;
    }

    public static boolean isFutureMaturity(Calendar oCalendar) {
        int month = oCalendar.get(Calendar.MONTH);
        if (month % 3 != 2)
            return false;

        if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
            return false;

        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        return (day - 7) >= 1 && (day - 7) <= 7;
    }

    public static boolean isDollarFutureMaturity(Calendar oCalendar) {
        if (oCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
            return false;

        int day = oCalendar.get(Calendar.DAY_OF_MONTH);

        return (day - 7) >= 8 && (day - 7) <= 15;
    }

    public static double getTimeToMaturity2(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        double totalHours = 252 * 6;

        double diffHours = 15 - oCalendar.get(Calendar.HOUR_OF_DAY);
        double hours = (diffHours < 0) ? 0 : diffHours;

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                hours += 6;        // �Ϸ� �ŷ��ð� 6�ð�

                if (isMaturity(oCalendar))
                    break;
            }
        }

        return hours / totalHours;
    }

    public static double getTimeToMaturity(Long time) {
        Calendar oCalendar = new GregorianCalendar();
        oCalendar.setTimeInMillis(time);

        double diffHours = 15 - oCalendar.get(Calendar.HOUR_OF_DAY);
        double hours = (diffHours < 0) ? 0 : diffHours;

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                hours += 6;        // �Ϸ� �ŷ��ð� 6�ð�

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        hours += 30;
                    }

                    break;
                }
            }
        }

        return hours / TOTAL_HOURS;
    }

    public static double getTimeToMaturity(Long time, int expireMonth) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        double totalHours = 252 * 6;

        double diffHours = 15 - oCalendar.get(Calendar.HOUR_OF_DAY);
        double hours = (diffHours < 0) ? 0 : diffHours;

        if (!isMaturity(oCalendar, expireMonth)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                hours += 6;        // �Ϸ� �ŷ��ð� 6�ð�

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar, expireMonth))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        hours += 30;
                    }

                    break;
                }
            }
        }

        return hours / totalHours;
    }

    public static double getTimeToMaturityByMinute(Long time, int expireMonth) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        double totalHours = 252 * 6 * 60;

        double diffMins = 0;

        diffMins = (15 * 60 + 10) - (oCalendar.get(Calendar.HOUR_OF_DAY) * 60 + oCalendar.get(Calendar.MINUTE) + oCalendar.get(Calendar.SECOND) / 60.0);

        double mins = diffMins;

        if (!isMaturity(oCalendar, expireMonth)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                mins += 6 * 60;        // �Ϸ� �ŷ��ð� 6�ð�

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar, expireMonth))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        mins += 5 * 6 * 60;
                    }

                    break;
                }
            }
        }

        return mins / totalHours;
    }

    public static double getAnnumDateToFutureMaturity(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int totalDays = 252;

        int days = 0;

        if (!isFutureMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isFutureMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 5;
                    }

                    break;
                }
            }
        }

        return (days * 1.0) / (totalDays * 1.0);
    }

    public static double getAnnumDateToOptionMaturity(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int totalDays = 252;

        int days = 0;

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 5;
                    }

                    break;
                }
            }
        }

        return (days * 1.0) / (totalDays * 1.0);
    }

    public static long getDayToOptionMautiry(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int days = 0;

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 5;
                    }

                    break;
                }
            }
        }

        return days;
    }

    public static double getAnnumDateToFutureMaturity365Base(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int totalDays = 365;

        int days = 0;

        if (!isFutureMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isFutureMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 7;
                    }

                    break;
                }
            }
        }

        return (days * 1.0) / (totalDays * 1.0);
    }

    public static double getAnnumDateToDollarFutureMaturity365Base(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int totalDays = 365;

        int days = 0;

        if (!isFutureMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {

                    while (true) {

                        if (isDollarFutureMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 7;
                    }

                    break;
                }
            }
        }

        return (days * 1.0) / (totalDays * 1.0);
    }

    public static double getAnnumDateToOptionMaturity365Base(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int totalDays = 365;

        int days = 0;

        if (!isMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 7;
                    }

                    break;
                }
            }
        }

        return (days * 1.0) / (totalDays * 1.0);
    }


    public static int getDayToFutureMaturity(Long time) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);


        int days = 0;

        if (!isFutureMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isFutureMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 5;
                    }

                    break;
                }
            }
        }

        return days;
    }


    public static double getAnnumTimeToFutureMaturity(Long time, boolean ignoreHoliday) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int daysPerYear;
        if (ignoreHoliday) {
            daysPerYear = 252;
        } else {
            daysPerYear = 365;
        }


        int days = 0;

        if (!isFutureMaturity(oCalendar)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                if (ignoreHoliday) {
                    // �ָ��� �ǳʶڴ�
                    if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                            oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                        continue;
                }

                days += 1;

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isFutureMaturity(oCalendar))
                            break;

                        oCalendar.add(Calendar.DATE, 7);
                        if (ignoreHoliday)
                            days += 5;
                        else
                            days += 7;
                    }

                    break;
                }
            }
        }

        return days * 1.0 / daysPerYear;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
//		BlackScholesImpVol blackScholesImpVol = new BlackScholesImpVol();

//		double time = getTimeToMaturity(System.currentTimeMillis());
//
        try {

            while (true) {
                String code = "4205K8260";
//				String isinCode = "KR4301F32703";
                long start = System.currentTimeMillis();
                long startNano = System.nanoTime();
                double underlyingPrice = 24745;
                double optionPrice = 10;
                double d = getDelta(code, underlyingPrice, optionPrice, start);
                double g = getGamma(code, underlyingPrice, optionPrice, start);
                double t = getThetaWithImpVol(code, underlyingPrice, optionPrice, start) / 365.0;
                double v = getDiscreteVega(code, underlyingPrice, optionPrice, start);
                double v2 = getContinuousVega(code, underlyingPrice, optionPrice, start);
                long endNano = System.nanoTime();
                System.out.println("Theta : " + t);
                System.out.println("Delta : " + d);
                System.out.println("Gamma : " + g);
                System.out.println("Vega D : " + v);
                System.out.println("Vega C : " + v2);
                System.out.println("Elapsed timeNano :" + (endNano - startNano));
                Thread.sleep(1000);
            }
        } catch (UnidentifiedStockCodeException e) {
            // TODO Auto-generated catch block
            DefaultLogger.logger.error("error found", e);
        } catch (NoBidAskDataException e) {
            DefaultLogger.logger.error("error found", e);
        }


        long time = System.currentTimeMillis();

        System.out.println("---- Test -----");
        System.out.println(getTimeToMaturity(time));
        System.out.println(getTimeToMaturity(time, 3));
        System.out.println(getTimeToMaturity2(time));
        System.out.println(getAnnumDateToFutureMaturity(time));
    }

    public static Double getBSoptionPrice(String code, double expectedUnderlyingPrice, Long time, double vol) throws UnidentifiedStockCodeException {

        String identifier = code.substring(0, 6);

        CallPut putCallId = getCallPutId(code);
        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '2' || strikePriceString.charAt(2) == '7') {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));


//		if ( isinCode.indexOf("KR4201F52802") >= 0 )
//			System.out.println(isinCode.substring(8,11) + " " + timeToMaturity + " "  + vol);
        double expectedOptionPrice =
                BS.bs_formula(putCallId, expectedUnderlyingPrice, strikePrice * 100, timeToMaturity
                        , BlackScholesImpVol.RFR, BlackScholesImpVol.CARRY, vol);

        return expectedOptionPrice;
    }


    public static Double getBS(String code, double underlyingPrice, double vol, Long time) throws UnidentifiedStockCodeException {
        String identifier = code.substring(0, 6);

        CallPut putCallId = getCallPutId(code);


        String strikePriceString = code.substring(8, 11);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);


        return BS.bs_formula(putCallId, underlyingPrice, strikePrice, timeToMaturity, CARRY, RFR, vol);
    }


    public static int getDayToMaturityIgnoreWeekend(long time, int expireMonth) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int days = 0;

        if (!isMaturity(oCalendar, expireMonth)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);

                // �ָ��� �ǳʶڴ�
                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    continue;

                days += 1;        // �Ϸ� �ŷ��ð� 6�ð�

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar, expireMonth))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 5;
                    }

                    break;
                }
            }
        }

        return days;
    }

    public static int getDayToMaturityIncludeWeekend(long time, int expireMonth) {
        Calendar oCalendar = new GregorianCalendar();

        oCalendar.setTimeInMillis(time);

        int days = 0;

        if (!isMaturity(oCalendar, expireMonth)) {
            while (true) {
                oCalendar.add(Calendar.DATE, 1);


                days += 1;        // �Ϸ� �ŷ��ð� 6�ð�

                if (oCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {

                    while (true) {

                        if (isMaturity(oCalendar, expireMonth))
                            break;

                        oCalendar.add(Calendar.DATE, 7);

                        days += 7;
                    }

                    break;
                }
            }
        }

        return days;
    }

    public static double getGamma(String code, double underlyingPrice, Long time, double vol) throws UnidentifiedStockCodeException {


        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);

        return BlackScholesImpVol.GGamma(underlyingPrice / 100.0, strikePrice, timeToMaturity, CARRY, vol);
    }


    public static double getGamma(String code, double underlyingPrice, double optionPrice, Long time) throws UnidentifiedStockCodeException {

        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));

        double v = BS.bs_impliedVol(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionPrice / 100.0);

        return BlackScholesImpVol.GGamma(underlyingPrice / 100.0, strikePrice, timeToMaturity, CARRY, v);
    }

    public static double getDelta(String code, double underlyingPrice, double optionPrice, Long time) throws UnidentifiedStockCodeException {
        String identifier = code.substring(0, 6);

        CallPut putCallId = getCallPutId(code);


        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));


        double v = BS.bs_impliedVol(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionPrice / 100.0);
        return BlackScholesImpVol.GDelta(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, v);
    }

    public static double getDelta(String code, double underlyingPrice, Long time, double vol) throws UnidentifiedStockCodeException {

        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(8, 11);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        Double timeToMaturity = getTimeToMaturity(time, expireMonth);


        return BlackScholesImpVol.GDelta(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, vol);
    }


    public static double getDelta(String code, long time, long futurePrice, long optionAskPrice) throws UnidentifiedStockCodeException {


        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));

        double FuturePrice = futurePrice;

        double v = BS.bs_impliedVol(putCallId, FuturePrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionAskPrice / 100.0);
        return GDelta(putCallId, FuturePrice / 100, strikePrice, timeToMaturity, 0.05, 0.05, v);
    }


    public static double getDelta(String code, double underlyingPrice, long time, double volatility) throws UnidentifiedStockCodeException {


        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);


        return BlackScholesImpVol.GDelta(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, volatility);
    }

    public static double getContinuousVega(String code, double underlyingPrice, double optionPrice, long time) throws UnidentifiedStockCodeException, NoBidAskDataException {


        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);

        double v = BS.bs_impliedVol(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionPrice / 100.0);

//		return BlackScholesImpVol.GTheta(putCallId, futurePrice/ 100.0, strikePrice, timeToMaturity, RFR, CARRY, v);

        return BlackScholesImpVol.GVega(underlyingPrice / 100.0, strikePrice, timeToMaturity, CARRY, v);

    }

    public static double getContinuousVega(String code, double futurePrice, long time, double vol) throws UnidentifiedStockCodeException {


        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);


//		return BlackScholesImpVol.GTheta(putCallId, futurePrice/ 100.0, strikePrice, timeToMaturity, RFR, CARRY, v);

        return BlackScholesImpVol.GVega(futurePrice / 100.0, strikePrice, timeToMaturity, CARRY, vol);

    }

    public static double getDiscreteVega(String code, double underlyingPrice, double optionPrice, long time) throws UnidentifiedStockCodeException {


        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);

        double v = BS.bs_impliedVol(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionPrice / 100.0);


        double priceNow = BS.bs_formula(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, v);
        double priceFuture = BS.bs_formula(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, v + 0.01);

        return priceFuture - priceNow;
    }

    public static double getThetaWithImpVol(String code, double underlyingPrice, double optionPrice, Long time) throws UnidentifiedStockCodeException {


        CallPut putCallId = getCallPutId(code);

        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        long expiryId = ExpiryCenter.Instance.getExpiryId(code);
        double timeToMaturity = TimeTillExpiryCenter.Instance.getTimeTillExpiry(time, expiryId);
//        double timeToMaturity = TimeTillMaturity.getTimeTillMaturity(time, code.charAt(6), code.charAt(7));

        double v = BS.bs_impliedVol(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, optionPrice / 100.0);

        return BlackScholesImpVol.GTheta(putCallId, underlyingPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, v);


    }

    public static double getTheta(String code, double underlyinPrice, Long time, double vol) throws UnidentifiedStockCodeException {

        CallPut putCallId = getCallPutId(code);


        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);


//		System.out.println(" UnderLying Price :" + futurePrice);
//		System.out.println(" Option Price :" + makretPriceOfOption);
        return BlackScholesImpVol.GTheta(putCallId, underlyinPrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, vol);


    }

    public static double getTheta(String code, Long time, long futurePrice, double vol) throws UnidentifiedStockCodeException {

        CallPut putCallId = getCallPutId(code);


        String strikePriceString = code.substring(6, 9);
        double strikePrice = Double.parseDouble(strikePriceString);
        if (strikePriceString.charAt(2) == '0' || strikePriceString.charAt(2) == '5') {

        } else {
            strikePrice += 0.5;
        }

        int expireMonth = getExpireMonth(code);
        double timeToMaturity = getTimeToMaturity(time, expireMonth);


//		System.out.println(" UnderLying Price :" + futurePrice);
//		System.out.println(" Option Price :" + makretPriceOfOption);
        return BlackScholesImpVol.GTheta(putCallId, futurePrice / 100.0, strikePrice, timeToMaturity, RFR, CARRY, vol);


    }

    public static int getExpireMonth(String code) {
        char month = code.charAt(7);

        char[] m = {month};

        Integer expireMonth;
        if (month < 58)
            expireMonth = Integer.parseInt(new String(m));
        else if (month == 'a' || month == 'A')
            expireMonth = 10;
        else if (month == 'b' || month == 'B')
            expireMonth = 11;
        else if (month == 'c' || month == 'C')
            expireMonth = 12;
        else
            expireMonth = null;

        return expireMonth;
    }

}
