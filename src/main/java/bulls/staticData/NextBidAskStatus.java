package bulls.staticData;

public enum NextBidAskStatus {
    UP,
    DOWN,
    STATIONARY;

    public boolean isRealized(UpDown upDown) {
        return this == UP && upDown == UpDown.UP || this == DOWN && upDown == UpDown.DOWN;
    }
}
