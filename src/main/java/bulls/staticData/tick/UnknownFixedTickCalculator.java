package bulls.staticData.tick;

import bulls.staticData.UpDown;

public class UnknownFixedTickCalculator extends FixedTickCalculator {
    private static final UnknownFixedTickCalculator instance = new UnknownFixedTickCalculator();

    public static UnknownFixedTickCalculator getInstance() {
        return instance;
    }

    private UnknownFixedTickCalculator() {
        super(1, true);
    }

    @Override
    public int getTickStartPrice(UpDown upDown, int currPrice) {
        return 0;
    }

    @Override
    public int getTickEndPrice(UpDown upDown, int currPrice) {
        return 0;
    }
}
