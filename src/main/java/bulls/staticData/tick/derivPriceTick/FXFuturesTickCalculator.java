package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class FXFuturesTickCalculator extends FixedTickCalculator {
    private static final FXFuturesTickCalculator instance = new FXFuturesTickCalculator();

    public static FXFuturesTickCalculator getInstance() {
        return instance;
    }

    private FXFuturesTickCalculator() {
        super(10, false);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 10;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
