package main;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/my_database");
        dataSource.setUsername("root");
        dataSource.setPassword("1111");
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() throws SQLException {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}