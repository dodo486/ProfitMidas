package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class Kospi200MiniOptionTickCalculator extends VariableTickCalculator {
    private static final Kospi200MiniOptionTickCalculator instance = new Kospi200MiniOptionTickCalculator();

    public static Kospi200MiniOptionTickCalculator getInstance() {
        return instance;
    }

    protected Kospi200MiniOptionTickCalculator() {
        super(new int[][]{
                {1, 1},
                {300, 2},
                {1000, 5},
                {Integer.MAX_VALUE, 5},
        });
    }
}
