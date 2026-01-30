package shopbancancau.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/shopbancancau?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    //private static final String PASSWORD = ""; //Vinh

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Kết nối MySQL thành công");
            return conn;
        } catch (Exception e) {
            System.out.println("❌ Kết nối MySQL thất bại");
            e.printStackTrace();
            return null;
        }
    }
}