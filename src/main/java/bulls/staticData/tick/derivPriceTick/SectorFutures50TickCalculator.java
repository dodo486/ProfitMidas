package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class SectorFutures50TickCalculator extends FixedTickCalculator {
    private static final SectorFutures50TickCalculator instance = new SectorFutures50TickCalculator();

    public static SectorFutures50TickCalculator getInstance() {
        return instance;
    }

    private SectorFutures50TickCalculator() {
        super(50, true);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 50;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
