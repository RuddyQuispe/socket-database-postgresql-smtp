import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initilize project");
        String url = "jdbc:postgresql://localhost:5432/beware_software";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "216042021");
        try {
            Connection conn = DriverManager.getConnection(url, props);
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from possible_victim");
            while (resultSet.next()) {
                System.out.printf("%-30.30s  %-30.30s%n", resultSet.getString("nickname"), resultSet.getString("cix_victim"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
