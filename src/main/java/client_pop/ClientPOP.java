package client_pop;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @class client for pop service, this class its used
 */
public class ClientPOP {
    /**
     * Attributes
     */
    private String host;
    private int port;
    private String userAccount;
    private Socket socketClient;
    private BufferedReader input;
    private DataOutputStream output;

    /**
     * @param host host server
     * @param port port enable
     * @constructor Initialize service POP and socket open
     */
    public ClientPOP(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            // open socket and connect POP service
            this.socketClient = new Socket(this.host, this.port);
            this.input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            this.output = new DataOutputStream(this.socketClient.getOutputStream());
            System.out.println("SERVER: " + this.input.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect POP services email and verify session login to email
     *
     * @param userAccount username email account to see
     * @param password    user account password
     * @return true if was started session or not if has a problem
     */
    public boolean login(String userAccount, String password) {
        if (this.socketClient != null && this.input != null && this.output != null && userAccount != "") {
            try {
                this.userAccount = userAccount;
                // send username for session
                String command = "USER " + this.userAccount + "\r\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.input.readLine());
                // send password for auth login session
                command = "PASS " + password + "\r\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.input.readLine());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Don't response server, We have problems to connect");
                return false;
            }
        } else {
            System.out.println("Error to connect POP service... again start service please");
            return false;
        }
    }

    /**
     * Get a list email
     *
     * @return List Email
     */
    public String getListEmail() {
        try {
            String command = "LIST\r\n";
            System.out.println("CLIENT: " + command);
            this.output.writeBytes(command);
            return "SERVER: " + this.getMultiline(this.input) + "\r\n";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error to get email list");
            return null;
        }
    }

    /**
     * Get email specified for read message and this data
     *
     * @param value id email
     * @return message email or null if value not validated
     */
    public String getEmailIDReceive(int value) {
        try {
            String command = "RETR " + value + "\n";
            System.out.println("CLIENT: " + command);
            this.output.writeBytes(command);
            return "SERVER: " + this.getMultiline(this.input) + "\r\n";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in RETR");
            return null;
        }
    }

    /**
     * Close session to POP service
     *
     * @return true of session closed success
     */
    public boolean closeSession() {
        try {
            String command = "QUIT\r\n";
            System.out.println("CLIENT: " + command);
            this.output.writeBytes(command);
            System.out.println("SERVER: " + this.input.readLine() + "\r\n");
            this.output.close();
            this.input.close();
            this.socketClient.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error en cerrar session");
            return false;
        }
    }

    /**
     * Get Message multiline
     *
     * @param in bufferReader
     * @return String meesage
     * @throws IOException
     * @author Ing. Evans Valcazar {evansvb@gmail.com}
     */
    private String getMultiline(BufferedReader in) throws IOException {
        String lines = "";
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.equals(".")) {
                // No more lines in the server response
                break;
            }
            if ((line.length() > 0) && (line.charAt(0) == '.')) {
                // The line starts with a "." - strip it off.
                line = line.substring(1);
            }
            // Add read line to the list of lines
            lines = lines + "\n" + line;
        }
        return lines;
    }

    public static void main(String[] args) {
        ClientPOP popClient = new ClientPOP("mail.tecnoweb.org.bo", 110);
        popClient.login("grupo01sa", "grup001grup001");
        String getListEmails = popClient.getListEmail();
        System.out.println(getListEmails);
        String email = popClient.getEmailIDReceive(47);
        System.out.println(email);
        popClient.closeSession();
    }
}
