package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Kospi200MiniFuturesTickCalculator extends FixedTickCalculator {
    private static final Kospi200MiniFuturesTickCalculator instance = new Kospi200MiniFuturesTickCalculator();

    public static Kospi200MiniFuturesTickCalculator getInstance() {
        return instance;
    }

    private Kospi200MiniFuturesTickCalculator() {
        super(2, true);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 2;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
