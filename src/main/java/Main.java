import client_pop.ClientPOP;
import client_smtp.ClientSMTP;
import database.ConnectionSQL;
import io.github.cdimascio.dotenv.Dotenv;
import wagu.Board;
import wagu.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static boolean sendEmail(String host, int port, String transmitter, String receptor, String subject, String body) {
        ClientSMTP clientSMTP = new ClientSMTP(host, port, transmitter);
        // evaluate message
        String responseQuery = getQuerySQL(subject);
        // send email
        if (clientSMTP.sendMail(subject, responseQuery, receptor)) {
            // close session smtp
            if (clientSMTP.closeServiceSMTP()) {
                System.out.println("CLOSED SERVICE");
            } else {
                System.out.println("FAILED TO CLOSE SESSION");
            }
            return true;
        } else {
            System.out.println("FAILED TO SEND EMAIL");
            return false;
        }
    }

    public static int getLastIDEmail(String s) {
        String result = s.substring(s.indexOf("OK") + 2, s.indexOf("mess"));
        return Integer.valueOf(result.trim());
    }

    public static String getSubjectEmail(String emailMessage) {
        String result = emailMessage.substring(emailMessage.indexOf("Subject:") + 8);
        return result.substring(0, result.indexOf("\n"));
    }

    public static String getQuerySQL(String patronQuery) {
        Dotenv dotenv = Dotenv.configure()
                .directory("/home/ruddy/Development/java/socket-database-postgresql-smtp/src/main/java/security/.env")
                .load();
        // get data for connection DB
        String hostDB = dotenv.get("DB_HOST");
        String portDB = dotenv.get("DB_PORT");
        String databaseDB = dotenv.get("DB_DATABASE");
        String usernameDB = dotenv.get("DB_USERNAME");
        String passwordDB = dotenv.get("DB_PASSWD");
        ConnectionSQL connectionDB = new ConnectionSQL(hostDB, Integer.valueOf(portDB), databaseDB, usernameDB, passwordDB);
        ResultSet sqlQuery = connectionDB.executeQuerySQL("select * " +
                "from persona " +
                "where per_nom like '%" + patronQuery + "%' or per_appm like '%" + patronQuery + "%' or " +
                "per_email like '%" + patronQuery + "%' or per_dir like '%" + patronQuery + "%';");
        List<String> headersList = Arrays.asList("per_nom", "per_appm","per_email", "per_dir");
        try {
            List<List<String>> rowsList = new ArrayList<List<String>>();
            while (sqlQuery.next()) {
                rowsList.add(Arrays.asList(
                    sqlQuery.getString("per_nom").trim(),
                    sqlQuery.getString("per_appm").trim(),
                    sqlQuery.getString("per_email").trim(),
                    sqlQuery.getString("per_dir").trim()
                ));
            }
            Board board = new Board(130);
            String tableString = board.setInitialBlock(new Table(board, 130, headersList, rowsList).
                    tableToBlocks()).build().getPreview();
            return tableString;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("database query problems");
            return "database query problems";
        }
    }

    public static void readLastEmail(String host, int port, String username, String password) {
        ClientPOP clientPOP = new ClientPOP(host, port);
        // initialize session login
        if (clientPOP.login(username, password)) {
            System.out.println("SUCCESS");
            // get list emails received
            String list = clientPOP.getListEmail();
            System.out.println(list);
            // get last ID of email
            int idLastEmail = getLastIDEmail(list);
            // get message email of email ID
            list = clientPOP.getEmailIDReceive(idLastEmail);
            // print email with sql query and info
            String stringAddictional = list.substring(list.indexOf("Subject:") + 8, list.length() - 1);
            // remove body email contained
            System.out.println(stringAddictional);
            // close session pop service
            if (clientPOP.closeSession()) {
                System.out.println("CLOSED SESSION");
            } else {
                System.out.println("FAILED CLOSE SESSION");
            }
        } else {
            System.out.println("Error to login");
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Hello TECNOWEB! 😂");
            Dotenv dotenv = Dotenv.configure()
                    .directory("/home/ruddy/Development/java/socket-database-postgresql-smtp/src/main/java/security/.env")
                    .load();
            // get data for connection SMTP
            String hostSMTP = dotenv.get("SMTP_HOST");
            String portSMTP = dotenv.get("SMTP_PORT");
            String transmitterSMTP = dotenv.get("SMTP_EMAIL_TRANSMITTER");
            String receptorSMTP = dotenv.get("SMTP_EMAIL_RECEPTOR");

            // get data for connection POP
            String hostPOP = dotenv.get("POP_HOST");
            String portPOP = dotenv.get("POP_PORT");
            String usernameReceptorPOP = dotenv.get("POP_USERNAME_RECEPTOR");
            String passwordReceptorPOP = dotenv.get("POP_PASSWD_RECEPTOR");

            // initialize sent and receive email
            if (sendEmail(hostSMTP, Integer.valueOf(portSMTP), transmitterSMTP, receptorSMTP, "sa", "consulta sql de prueba")) {
                System.out.println("EMAIL SENT");
                readLastEmail(hostPOP, Integer.valueOf(portPOP), usernameReceptorPOP, passwordReceptorPOP);
            } else {
                System.out.println("EMAIL NO SENT");
                return;
            }
        } catch (Exception exception) {
            System.out.println("ERROR INTO MAIN VOID: " + exception);
            return;
        }
    }
}
