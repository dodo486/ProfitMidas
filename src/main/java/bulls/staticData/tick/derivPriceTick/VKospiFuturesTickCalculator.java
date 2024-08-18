package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class VKospiFuturesTickCalculator extends FixedTickCalculator {
    private static final VKospiFuturesTickCalculator instance = new VKospiFuturesTickCalculator();

    public static VKospiFuturesTickCalculator getInstance() {
        return instance;
    }

    private VKospiFuturesTickCalculator() {
        super(5, true);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 5;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}