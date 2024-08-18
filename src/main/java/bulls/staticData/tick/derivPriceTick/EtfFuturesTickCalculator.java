package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class EtfFuturesTickCalculator extends FixedTickCalculator {
    private static final EtfFuturesTickCalculator instance = new EtfFuturesTickCalculator();

    public static EtfFuturesTickCalculator getInstance() {
        return instance;
    }

    private EtfFuturesTickCalculator() {
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
