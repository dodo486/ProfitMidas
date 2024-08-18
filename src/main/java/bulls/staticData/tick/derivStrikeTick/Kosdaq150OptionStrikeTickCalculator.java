package bulls.staticData.tick.derivStrikeTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Kosdaq150OptionStrikeTickCalculator extends FixedTickCalculator {
    private static final Kosdaq150OptionStrikeTickCalculator instance = new Kosdaq150OptionStrikeTickCalculator();

    public static Kosdaq150OptionStrikeTickCalculator getInstance() {
        return instance;
    }

    private Kosdaq150OptionStrikeTickCalculator() {
        super(2500, false);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 2500;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
