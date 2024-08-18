package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class Kospi200BigOptionTickCalculator extends VariableTickCalculator {
    private static final Kospi200BigOptionTickCalculator instance = new Kospi200BigOptionTickCalculator();

    public static Kospi200BigOptionTickCalculator getInstance() {
        return instance;
    }

    protected Kospi200BigOptionTickCalculator() {
        super(new int[][]{
                {1, 1},
                {1000, 5},
                {Integer.MAX_VALUE, 5},
        });
    }
}
