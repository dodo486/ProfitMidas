package bulls.staticData.tick.derivStrikeTick;

import bulls.staticData.UpDown;
import bulls.staticData.tick.FixedTickCalculator;

public class Kospi200OptionStrikeTickCalculator extends FixedTickCalculator {
    private static final Kospi200OptionStrikeTickCalculator instance = new Kospi200OptionStrikeTickCalculator();

    public static Kospi200OptionStrikeTickCalculator getInstance() {
        return instance;
    }

    private Kospi200OptionStrikeTickCalculator() {
        super(250, false);
    }

    public static int getClosestStrike(int price) {
        int floor = price / 250 * 250;
        int ceil = floor + 250;

        if (ceil - price <= price - floor)
            return ceil;
        else
            return floor;
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 250;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return Integer.MAX_VALUE;
    }
}
