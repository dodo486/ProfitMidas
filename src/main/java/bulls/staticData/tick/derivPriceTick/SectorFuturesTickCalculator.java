package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class SectorFuturesTickCalculator extends FixedTickCalculator {
    private static final SectorFuturesTickCalculator instance = new SectorFuturesTickCalculator();

    public static SectorFuturesTickCalculator getInstance() {
        return instance;
    }

    private SectorFuturesTickCalculator() {
        super(20, true);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 20;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
