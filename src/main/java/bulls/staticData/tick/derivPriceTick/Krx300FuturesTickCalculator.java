package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Krx300FuturesTickCalculator extends FixedTickCalculator {
    private static final Krx300FuturesTickCalculator instance = new Krx300FuturesTickCalculator();

    public static Krx300FuturesTickCalculator getInstance() {
        return instance;
    }

    private Krx300FuturesTickCalculator() {
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
