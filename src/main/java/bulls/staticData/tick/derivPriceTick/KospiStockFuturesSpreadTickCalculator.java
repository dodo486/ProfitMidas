package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class KospiStockFuturesSpreadTickCalculator extends FixedTickCalculator {
    public KospiStockFuturesSpreadTickCalculator(int tickSize, boolean isNegativePriceAllowed) {
        super(tickSize, isNegativePriceAllowed);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return -1000000 * tickSize;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return 1000000 * tickSize;
    }
}
