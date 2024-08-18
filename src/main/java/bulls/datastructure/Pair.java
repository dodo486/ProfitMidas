package bulls.datastructure;

public final class Pair<T, V> {

    public final T firstElem;
    public final V secondElem;


    @Override
//    @SuppressWarnings("cast")
    public boolean equals(Object o) {

        if (!(o instanceof Pair))
            return false;
        Pair<T, V> temp = (Pair<T, V>) o;
        return firstElem.equals(temp.firstElem) && secondElem.equals(temp.secondElem);

    }

    @Override
    public int hashCode() {
        int hashCode = firstElem.hashCode() + secondElem.hashCode();
        return hashCode;

    }

    public Pair(T t, V v) {
        firstElem = t;
        secondElem = v;
    }

    @Override
    public String toString() {
        return "(" + firstElem + ", " + secondElem + ")";
    }
}