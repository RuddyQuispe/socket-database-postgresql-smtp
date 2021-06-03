import database.ConnectionSQL;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello TECNOWEB! 😂");
        Dotenv dotenv = Dotenv.configure()
                .directory("C:\\Users\\Ruddy\\IdeaProjects\\socket-database-postgresql\\src\\main\\java")
                .load();
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String database = dotenv.get("DB_DATABASE");
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWD");

        ConnectionSQL connectionDB = new ConnectionSQL(host, Integer.valueOf(port), database, username, password);
        connectionDB.executeQuerySQL("");
    }
}
