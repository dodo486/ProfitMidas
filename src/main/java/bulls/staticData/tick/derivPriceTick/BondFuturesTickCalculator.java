package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class BondFuturesTickCalculator extends FixedTickCalculator {
    private static final BondFuturesTickCalculator instance = new BondFuturesTickCalculator();

    public static BondFuturesTickCalculator getInstance() {
        return instance;
    }

    private BondFuturesTickCalculator() {
        super(1, false);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 1;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
