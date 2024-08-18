package bulls.tool.util;

import bulls.datastructure.BidAskData;
import bulls.datastructure.Pair;
import bulls.exception.NotEnoughBidAskAmountException;
import org.apache.commons.math3.util.FastMath;

import java.util.LinkedList;
import java.util.Queue;


public abstract class SimpleMath {

    public static int removeDecimalPoint(double f) {
        return (int) f;
    }

    public static int ceil(double f) {
        int r = (int) f;

        if (f - r > 0)
            r++;

        return r;
    }


    public static int round(double f) {
        int r = (int) f;

        if (f - r >= 0.5)
            r++;

        return r;
    }


    public static long power(long root, long power) {
        long ret = 1;
        for (int i = 0; i < power; i++) {
            ret = ret * root;
        }
        return ret;
    }

    public static double power(double root, long power) {
        double ret = 1;
        for (int i = 0; i < power; i++) {
            ret = ret * root;
        }
        return ret;
    }

    public static int weightedAverage(int[] prices, int[] amounts) {

        int length = prices.length;

        if (length != amounts.length)
            return 0;

        int totalMoney = 0;
        int totalAmount = 0;
        for (int i = 0; i < length; i++) {
            totalMoney += prices[i] * amounts[i];
            totalAmount += amounts[i];
        }

        return (totalMoney / totalAmount);

    }

    public static Long weightedAverageOfTwo(long priceA, long amountA, long priceB, long amountB) {

        return ((priceA * amountA) + (priceB * amountB)) / (amountA + amountB);
    }

    public static float weightedAverageOfTwo(float priceA, long amountA, long priceB, long amountB) {

        return ((priceA * amountA) + (priceB * amountB)) / (amountA + amountB);
    }

    public static Long calcPriceFromBidAsk(long[] ls, long[] ls2, Long totalAmount) throws NotEnoughBidAskAmountException {

        if (totalAmount == 0)
            return null;

        Long remainAmount = totalAmount;

        long sum = 0;
        for (int i = 0; i < ls2.length; i++) {
            if (ls2[i] > remainAmount) {
                sum += remainAmount * ls[i];
                remainAmount = 0L;
                break;
            } else {
                sum += ls2[i] * ls[i];
                remainAmount -= ls2[i];
            }

        }

        if (remainAmount != 0) {
            throw new NotEnoughBidAskAmountException(remainAmount, totalAmount);
        }
        Long ret = (sum / totalAmount);
        return ret;
    }


    public static Long calcPriceFromBidAsk(long[] ls, long[] ls2, Long totalAmount, String code) throws NotEnoughBidAskAmountException {

        Long remainAmount = totalAmount;

        long sum = 0;
        for (int i = 0; i < ls2.length; i++) {
            if (ls2[i] > remainAmount) {
                sum += remainAmount * ls[i];
                remainAmount = 0L;
                break;
            } else {
                sum += ls2[i] * ls[i];
                remainAmount -= ls2[i];
            }

        }

        if (remainAmount != 0) {
            throw new NotEnoughBidAskAmountException(remainAmount, code);
        }
        Long ret = (sum / totalAmount);
        return ret;
    }


    public static Long calcAmountFromBidAsk(long[] ls, long[] ls2, long netMoney) {

        Long remainMoney = netMoney;

        if (netMoney < ls[0])
            return 0L;

        long totalAmount = 0;
        for (int i = 0; i < ls2.length; i++) {
            if (ls2[i] * ls[i] > remainMoney) {
                totalAmount += remainMoney / ls[i];
                break;
            } else {
                totalAmount += ls2[i];
                remainMoney -= ls2[i] * ls[i];
            }

        }

        return totalAmount;
    }


    public static Pair<Integer, Integer> calcRelativeSameAmount(long money, int unitOrder1, int unitOrder2, long price1, long price2) {
        int min1 = (int) Math.floor(money * 1.0 / price1 / unitOrder1);
        int min2 = (int) Math.floor(money * 1.0 / price2 / unitOrder2);

        int max1 = (int) Math.ceil(money * 1.0 / price1 / unitOrder1);
        int max2 = (int) Math.ceil(money * 1.0 / price2 / unitOrder2);

        Long moneyMin1Min2 = Math.abs((min1 * unitOrder1 * price1) - (min2 * unitOrder2 * price2));
        Long moneyMin1Max2 = Math.abs((min1 * unitOrder1 * price1) - (max2 * unitOrder2 * price2));
        Long moneyMax1Min2 = Math.abs((max1 * unitOrder1 * price1) - (min2 * unitOrder2 * price2));
        Long moneyMax1Max2 = Math.abs((max1 * unitOrder1 * price1) - (max2 * unitOrder2 * price2));


        if (moneyMin1Min2 <= moneyMin1Min2 && moneyMin1Min2 <= moneyMin1Max2 && moneyMin1Min2 <= moneyMax1Min2 && moneyMin1Min2 <= moneyMax1Max2)
            return new Pair<Integer, Integer>(min1 * unitOrder1, min2 * unitOrder2);
        if (moneyMin1Max2 <= moneyMin1Min2 && moneyMin1Max2 <= moneyMin1Max2 && moneyMin1Max2 <= moneyMax1Min2 && moneyMin1Max2 <= moneyMax1Max2)
            return new Pair<Integer, Integer>(min1 * unitOrder1, max2 * unitOrder2);
        if (moneyMax1Min2 <= moneyMin1Min2 && moneyMax1Min2 <= moneyMin1Max2 && moneyMax1Min2 <= moneyMax1Min2 && moneyMax1Min2 <= moneyMax1Max2)
            return new Pair<Integer, Integer>(max1 * unitOrder1, min2 * unitOrder2);
        if (moneyMax1Max2 <= moneyMin1Min2 && moneyMax1Max2 <= moneyMin1Max2 && moneyMax1Max2 <= moneyMax1Min2 && moneyMax1Max2 <= moneyMax1Max2)
            return new Pair<Integer, Integer>(max1 * unitOrder1, max2 * unitOrder2);


        return null;

    }


    public static int calcStockProfit(int price, BidAskData ba, int amount) {
        if (ba == null)
            return 0;

        int remainAmount = amount;


        int profit = 0;
        for (int i = 0; i < ba.bidAmount.length; i++) {
            if (ba.bidAmount[i] > remainAmount) {
                profit += (ba.bidPrice[i] - price) * remainAmount;
                remainAmount = 0;
                break;
            } else {
                profit += (ba.bidPrice[i] - price) * ba.bidAmount[i];
                remainAmount -= ba.bidAmount[i];
            }

        }

        return profit;

    }

    //price has been already multiplied by 100
    public static long calcFutureProfit(long priceBuy, long priceSell, boolean isLong) {
        return calcFutureProfit(priceBuy, priceSell, 1, isLong);
    }

    public static long calcFutureProfit(long priceBuy, long priceSell, long amount, boolean isLong) {
        if (isLong)
            return (priceSell - priceBuy) * amount * 5000;
        else
            return (priceSell - priceBuy) * amount * 5000;
    }


    public static long calcFutureProfit(long priceBuy, long priceSell, long amount) {
        return (priceSell - priceBuy) * amount * 5000;
    }

    public static long calcFutureProfit(double priceBuy, double priceSell, long amount) {
        return Math.round((priceSell - priceBuy) * amount * 5000);
    }

    //price has been already multiplied by 100
    public static long calcStockProfit(long priceBuy, long priceSell, long amount, boolean isLong) {
        if (isLong)
            return (priceSell - priceBuy) * amount;
        else
            return (priceBuy - priceSell) * amount;
    }


    //price has been already multiplied by 100
    public static long calcOptionProfit(long priceLong, long priceShort, long amount) {
        return (priceShort - priceLong) * amount * 1000;

    }


    public static long calcOptionProfit(double priceLong, double priceShort, long amount) {
        return Math.round((priceShort - priceLong) * amount * 1000);

    }


    public static float calcAverage(Queue<Integer> q) {

        LinkedList<Integer> newQ = new LinkedList<Integer>(q);

        int size = newQ.size();
        if (size == 0)
            return 0;

        int total = 0;
        while (!newQ.isEmpty()) {
            int piece = newQ.pop();
            total += piece;
        }

        return (float) ((total * 1.0) / size);

    }


    public static Long equityFeeAndTax(Long buyPrice, Long sellPrice, Long amount, double feeRate, double taxRate) {
        // TODO Auto-generated method stub
        double fee = (buyPrice * amount * feeRate) + (sellPrice * amount * feeRate);
        double tax = (sellPrice * amount * taxRate);

        return (long) (fee + tax);
    }

    public static Long futureFeeAndTax(Long buyPrice, Long sellPrice, Long amount, Long multiplier, double feeRate, double taxRate) {
        // TODO Auto-generated method stub
        double fee = ((buyPrice / 100.0) * amount * multiplier * feeRate) + ((sellPrice / 100.0) * amount * multiplier * feeRate);
        double tax = ((sellPrice / 100.0) * amount * multiplier * taxRate);

        return (long) (fee + tax);
    }

    public static long parseLongWithSign(String input) {
        Long ret = null;
        if (input.charAt(0) == '+')
            input = input.substring(1);
        ret = Long.parseLong(input);
        return ret;

    }

    public static double calcNextUpperBoundary(long currentPriceA, long currentPriceB, int tickTimes) {

        long priceA = currentPriceA;
        long priceB = currentPriceB;
        double totalDeltaLogValue = 0;
        for (int i = 0; i < tickTimes; i++) {
            int unitPriceChangeA = getUnitPriceChangeStock(priceA);
            int unitPriceChangeB = getUnitPriceChangeStock(priceB);
            double current = Math.log(priceA * 1.0 / priceB);
            double plusTickA = Math.log((priceA + unitPriceChangeA) * 1.0 / priceB);
            double plusTickB = Math.log(priceA * 1.0 / (priceB - unitPriceChangeB));

            if (plusTickA > plusTickB) {
                totalDeltaLogValue += plusTickB - current;
                priceB = priceB - unitPriceChangeB;
            } else {
                totalDeltaLogValue += plusTickA - current;
                priceA = priceA + unitPriceChangeA;
            }

        }

        return totalDeltaLogValue;

    }


    public static double calcNextLowerBoundary(long currentPriceA, long currentPriceB, int tickTimes) {

        long priceA = currentPriceA;
        long priceB = currentPriceB;
        double totalDeltaLogValue = 0;
        for (int i = 0; i < tickTimes; i++) {
            int unitPriceChangeA = getUnitPriceChangeStock(priceA);
            int unitPriceChangeB = getUnitPriceChangeStock(priceB);
            double current = Math.log(priceA * 1.0 / priceB);
            double minusTickA = Math.log((priceA - unitPriceChangeA) * 1.0 / priceB);
            double minusTickB = Math.log(priceA * 1.0 / (priceB + unitPriceChangeB));

            if (minusTickA > minusTickB) {
                totalDeltaLogValue += current - minusTickB;
                priceB = priceB + unitPriceChangeB;
            } else {
                totalDeltaLogValue += current - minusTickA;
                priceA = priceA - unitPriceChangeA;
            }

        }

        return totalDeltaLogValue;

    }

//	
//	public static double calcNextLowerBoundary(long priceA,  long priceB){
//
//		int unitPriceChangeA = getUnitPriceChange(priceA);
//		int unitPriceChangeB = getUnitPriceChange(priceB);
//		double current = Math.log(priceA/priceB*1.0);
//		double minusTickA = Math.log((priceA-unitPriceChangeA)/priceB*1.0);
//		double minusTickB = Math.log(priceA/(priceB+unitPriceChangeB)*1.0);
//		
//		return Math.min(current-minusTickB, current-minusTickA);
//	}

    public static long getStockUnitPriceAdjustedPrice(long price, boolean adjustToCeil) {

        int unitPriceChange = getUnitPriceChangeStock(price);
        int adjustAmount = unitPriceChange / 2;


        if (price % unitPriceChange == 0)
            return price;
        if (price < 5000)
            return price;

        // ceil
        if (adjustToCeil) {
            return price + adjustAmount;
        } else {
            return price - adjustAmount;
        }
    }

    public static int getUnitPriceChangeStock(long price) {
        if (price < 5000)
            return 5;
        else if (price < 10000)
            return 10;
        else if (price < 50000)
            return 50;
        else if (price < 100000)
            return 100;
        else if (price < 500000)
            return 500;
        else
            return 1000;
    }

    public static int getUnitOrderAmountStock(long price) {

        if (price < 50000)
            return 10;
        else
            return 1;

    }


    public static long calcOptionMaturityPnL(long strikePrice, long maturityPrice, long enterPrice, long amount, boolean isCall) {

        long premium = enterPrice * -amount;

        long posPnL;
        if (isCall) {
            if (amount > 0)
                posPnL = Math.max(0, (maturityPrice - strikePrice) * amount);
            else
                posPnL = Math.min(0, (maturityPrice - strikePrice) * amount);

        } else {
            if (amount > 0)
                posPnL = Math.max(0, (strikePrice - maturityPrice) * amount);
            else
                posPnL = Math.min(0, (strikePrice - maturityPrice) * amount);
        }
//		System.out.println("행사가:" + strikePrice + " 만기지수:" + maturityPrice + 
//				" 진입가격 :" + enterPrice +" 수량 :" + amount + " 내재가치:" + (posPnL+premium) );
        return premium + posPnL;
    }


    public static double sigmoid(double x) {
        double sigmoid = 2 / (1 + FastMath.exp(-(4 * x - 4))) - 1;
        return sigmoid;
    }


}
