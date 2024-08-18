package bulls.designTemplate;

import com.google.common.collect.HashBasedTable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class UniqueStringPair implements Comparable<UniqueStringPair> {

    public final String first;
    public final String second;

    private UniqueStringPair(@NotNull String first, @NotNull String second) {
        this.first = first;
        this.second = second;
    }

    static HashBasedTable<String, String, UniqueStringPair> table = HashBasedTable.create();

    public static UniqueStringPair getOrCreate(String first, String second) {
        if (first == null || second == null)
            return null;

        UniqueStringPair pair = table.get(first, second);
        if (pair != null)
            return pair;

        pair = new UniqueStringPair(first, second);
        table.put(first, second, pair);
        return pair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniqueStringPair that = (UniqueStringPair) o;
        return first.equals(that.first) && second.equals(that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "UniqueStringPair{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull UniqueStringPair o) {
        int result = this.first.compareTo(o.first);
        if (result != 0)
            return result;

        return this.second.compareTo(o.second);
    }
}
