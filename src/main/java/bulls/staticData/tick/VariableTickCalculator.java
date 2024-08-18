package bulls.staticData.tick;

import bulls.staticData.UpDown;
import org.apache.commons.math3.util.FastMath;

public abstract class VariableTickCalculator implements TickFunction {
    //start price(inclusive) tick size pair
    protected int[][] tickSizes; // startPrice, tick, index, accumulatedIndex

    protected VariableTickCalculator(int[][] _tickSizes) {
        tickSizes = new int[_tickSizes.length][3];
        for (int i = 0; i < _tickSizes.length; ++i) {
            tickSizes[i][0] = _tickSizes[i][0];
            tickSizes[i][1] = _tickSizes[i][1];
            if (i < _tickSizes.length - 1)
                tickSizes[i][2] = (_tickSizes[i + 1][0] - _tickSizes[i][0]) / tickSizes[i][1];
            else
                tickSizes[i][2] = Integer.MAX_VALUE;
        }
        if (_tickSizes[_tickSizes.length - 1][0] != Integer.MAX_VALUE) {
            System.err.println("Last price of tickSizes array must be Integer.MAX_VALUE");
            System.exit(1);
        }
    }

    public int getTickSize(UpDown upDown, int currPrice) {
        if (upDown == UpDown.UP) {
            int currTick = GetBiggestTickSize();
            for (int i = 0; i < tickSizes.length; ++i) {
                if (currPrice < tickSizes[i + 1][0]) {
                    currTick = tickSizes[i][1];
                    return currTick;
                }
            }
            return currTick;
        } else {
            int currTick = GetBiggestTickSize();
            for (int i = 0; i < tickSizes.length; ++i) {
                if (currPrice <= tickSizes[i + 1][0]) {
                    currTick = tickSizes[i][1];
                    return currTick;
                }
            }
            return currTick;
        }
    }

    public int getTickStartPrice(UpDown upDown, int currPrice) {
        if (upDown == UpDown.UP) {
            int currTickStartPrice = GetBiggestTickStartPrice();
            for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
                if (currPrice < tickSizes[i + 1][0]) {
                    currTickStartPrice = tickSizes[i][0];
                    return currTickStartPrice;
                }
            }
            return currTickStartPrice;
        } else {
            int currTickStartPrice = GetBiggestTickStartPrice() + GetBiggestTickSize();
            for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
                if (currPrice < tickSizes[i + 1][0]) {
                    currTickStartPrice = tickSizes[i][0] + tickSizes[i][1];
                    return currTickStartPrice;
                }
            }
            return currTickStartPrice;
        }
    }

    public int getTickEndPrice(UpDown upDown, int currPrice) {
        if (upDown == UpDown.UP) {
            int currTickEndPrice = Integer.MAX_VALUE;
            for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
                if (currPrice < tickSizes[i + 1][0]) {
                    currTickEndPrice = tickSizes[i + 1][0] - tickSizes[i + 1][1];
                    return currTickEndPrice;
                }
            }
            return currTickEndPrice;
        } else {
            int currTickEndPrice = Integer.MAX_VALUE;
            for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
                if (currPrice < tickSizes[i + 1][0]) {
                    currTickEndPrice = tickSizes[i + 1][0];
                    return currTickEndPrice;
                }
            }
            return currTickEndPrice;
        }
    }

    public int getNearestNormalizedPrice(int price) {
        int tickSize = getTickSize(UpDown.UP, price);
        if (price % tickSize == 0) {
            return price;
        }
        int startPrice = getTickStartPrice(UpDown.UP, price);
        int tickCount = FastMath.round((price - startPrice) / (float) tickSize);
        return startPrice + tickSize * tickCount;
    }

    public int getNormalizedPrice(UpDown upDown, int price) {
        int tickSize = getTickSize(UpDown.UP, price);
        if (price % tickSize == 0)
            return price;
        int startPrice = getTickStartPrice(UpDown.UP, price);
        if (upDown == UpDown.UP) {
            int tickCount = (int) FastMath.ceil((price - startPrice) / (float) tickSize);
            return startPrice + tickSize * tickCount;
        } else {
            int tickCount = (int) FastMath.floor((price - startPrice) / (float) tickSize);
            return startPrice + tickSize * tickCount;
        }
    }

    public int getTickCountBetween(int toPrice, int fromPrice) {
        int ticks = 0;
        int bigPrice, smallPrice;
        boolean flag = toPrice > fromPrice;
        if (flag) {
            bigPrice = toPrice;
            smallPrice = fromPrice;
        } else {
            bigPrice = fromPrice;
            smallPrice = toPrice;
        }
        int currPrice = smallPrice;
        for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
            int nextTickStartPrice = tickSizes[i + 1][0];
            if (nextTickStartPrice >= currPrice) {
                int nextP;
                boolean finished = false;
                if (nextTickStartPrice > bigPrice) {
                    nextP = bigPrice;
                    finished = true;
                } else {
                    nextP = nextTickStartPrice;
                }
                int tickCount = Math.round((nextP - currPrice) / (float) tickSizes[i][1]);// MidpointRounding.AwayFromZero not needed because nextP-currPrice is positive
                ticks += tickCount;
                currPrice = nextP;
                if (finished)
                    break;
            }
        }
        return flag ? ticks : -ticks;
    }

    public int getPriceByTick(UpDown upDown, int currPrice, int tickCount) {
        int remainTick = tickCount;
        int p = currPrice;
        if (upDown == UpDown.UP) {
            int currTick;
            int currTickPrice;
            int nextTickPrice;
            for (int i = 0; i < GetEffectiveTickSizesArrayCount(); ++i) {
                nextTickPrice = tickSizes[i + 1][0];
                if (p < nextTickPrice) {
                    currTickPrice = tickSizes[i][0];
                    currTick = tickSizes[i][1];
                    int tickCountToNext;
                    if (p == currTickPrice)
                        tickCountToNext = tickSizes[i][2];
                    else
                        tickCountToNext = (nextTickPrice - p) / currTick;
                    if (tickCountToNext >= remainTick) {
                        return p + remainTick * currTick;
                    }
                    remainTick -= tickCountToNext;
                    p = nextTickPrice;
                }
            }
        } else {
            int currTick;
            int currTickPrice;
            int nextTickPrice;
            for (int i = GetEffectiveTickSizesArrayCount() - 1; i >= 0; --i) {
                currTickPrice = tickSizes[i][0];
                if (p > currTickPrice) {
                    nextTickPrice = tickSizes[i + 1][0];
                    currTick = tickSizes[i][1];
                    int tickCountToNext;
                    if (p == nextTickPrice)
                        tickCountToNext = tickSizes[i][2];
                    else
                        tickCountToNext = (p - currTickPrice) / currTick;
                    if (tickCountToNext >= remainTick) {
                        return p - remainTick * currTick;
                    }
                    remainTick -= tickCountToNext;
                    p = currTickPrice;
                }
            }
        }
        return p;
    }

    int GetEffectiveTickSizesArrayCount() {
        return tickSizes.length - 1;
    }

    int GetBiggestTickStartPrice() {
        return tickSizes[tickSizes.length - 2][0];
    }

    int GetBiggestTickSize() {
        return tickSizes[tickSizes.length - 2][1];
    }
}
