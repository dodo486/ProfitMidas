package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Kospi200BigFuturesTickCalculator extends FixedTickCalculator {
    private static final Kospi200BigFuturesTickCalculator instance = new Kospi200BigFuturesTickCalculator();

    public static Kospi200BigFuturesTickCalculator getInstance() {
        return instance;
    }

    private Kospi200BigFuturesTickCalculator() {
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
