package bulls.db.rdb;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLDBData {
    public String sql;
    public Object[] values;

    public boolean setParameter(PreparedStatement statement) {
        try {
            for (int i = 0; i < values.length; ++i) {
                if (values[i] instanceof int[][]) {
                    Array arr = statement.getConnection().createArrayOf("integer", (Object[]) values[i]);
                    statement.setArray(1 + i, arr);
                } else {
                    statement.setObject(1 + i, values[i]);
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
