package ni.edu.uam.practicajavafxypostresql.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DataBaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/BibiotecaFX";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Josetorres2005!";

    private DataBaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);

    }
}
