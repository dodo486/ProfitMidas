package bulls.staticData;
public enum UpDown {
    UP,
    DOWN;

    public UpDown opposite() {
        if (this == UP)
            return DOWN;
        else
            return UP;
    }
}
