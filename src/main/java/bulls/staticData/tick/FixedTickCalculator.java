package bulls.staticData.tick;

import bulls.staticData.UpDown;
import org.apache.commons.math3.util.FastMath;

public abstract class FixedTickCalculator implements TickFunction {
    final protected int tickSize;
    final protected boolean isNegativePriceAllowed;

    public FixedTickCalculator(int tickSize, boolean isNegativePriceAllowed) {
        this.tickSize = tickSize;
        this.isNegativePriceAllowed = isNegativePriceAllowed;
    }

    public int getTickSize(UpDown upDown, int currRegPrice) {
        return this.tickSize;
    }

    public abstract int getTickStartPrice(UpDown upDown, int currPrice);

    public abstract int getTickEndPrice(UpDown upDown, int currPrice);

    public int getPriceByTick(UpDown upDown, int currPrice, int tickCount) {
        if (upDown == UpDown.UP) {
            return currPrice + tickCount * tickSize;
        }
        return currPrice - tickCount * tickSize;
    }

    public int getTickCountBetween(int toPrice, int fromPrice) {
        return (toPrice - fromPrice) / tickSize;
    }

    public int getNearestNormalizedPrice(int price) {
        if (price % tickSize == 0)
            return price;
        int startPrice = getTickStartPrice(UpDown.UP, price);
        int tickCount = (int) FastMath.round((price - startPrice) / (double) tickSize);
        return startPrice + (tickCount) * tickSize;
    }

    public int getNormalizedPrice(UpDown upDown, int price) {
        if (price % tickSize == 0)
            return price;
        int startPrice = getTickStartPrice(UpDown.UP, price);
        int tickCount = ((price - startPrice) / tickSize);
        if (upDown == UpDown.UP)
            return startPrice + (1 + tickCount) * tickSize;
        else
            return startPrice + (tickCount) * tickSize;
    }
}
