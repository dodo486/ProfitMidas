package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Kosdaq150FuturesTickCalculator extends FixedTickCalculator {
    private static final Kosdaq150FuturesTickCalculator instance = new Kosdaq150FuturesTickCalculator();

    public static Kosdaq150FuturesTickCalculator getInstance() {
        return instance;
    }

    private Kosdaq150FuturesTickCalculator() {
        super(10, true);
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
