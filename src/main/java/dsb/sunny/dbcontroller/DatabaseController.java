package dsb.sunny.dbcontroller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseController {

    private final String url;

    public DatabaseController() {
        this.url = "jdbc:sqlite:./sunny_db.db";
    }

    public Connection borrowConection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
