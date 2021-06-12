package client_smtp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @class connection and SMTP service
 */
public class ClientSMTP {
    /**
     * Attributes
     */
    private String host;
    private int port;
    private String transmitter;
    private Socket clientSocket;
    private BufferedReader input;
    private DataOutputStream output;

    /**
     * @param host        server smtp ip or host
     * @param port        port enable
     * @param transmitter email from send emails
     * @constructor starts the connection to the server and waits
     * for the response to our request
     * throws crash an error if dont receive response at smtp server
     */
    public ClientSMTP(String host, int port, String transmitter) {
        this.host = host;
        this.port = port;
        this.transmitter = transmitter;
        // establishing the connection to the server with sockets and input and output buffers
        try {
            this.clientSocket = new Socket(this.host, this.port);
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.output = new DataOutputStream(this.clientSocket.getOutputStream());
            // response server about own connection
            System.out.println("SERVER: " + this.input.readLine());
            // waving to SMTP server
            String command = "EHLO " + this.host + "\r\n";
            System.out.println("CLIENT: " + command);
            this.output.writeBytes(command);
            System.out.println("SERVER: " + this.getMultiline(this.input));
        } catch (IOException e) {
            System.out.println("Error in enable connection to server SMTP");
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTransmitter() {
        return transmitter;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    /**
     * send an email with the initialized session, to another a recipient email
     * with the content of the message
     *
     * @param subject       email title
     * @param body          email container
     * @param emailReceptor email to send
     * @return if this email is sent or don't sent
     */
    public boolean sendMail(String subject, String body, String emailReceptor) {
        if (this.clientSocket != null && this.input != null && this.output != null) {
            try {
                // Sending the sender email (who will send the email)
                String command = "MAIL FROM: " + this.transmitter + "\r\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.input.readLine());
                // Sending the recipient email (who will receive the message)
                command = "RCPT TO: " + emailReceptor + "\r\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.input.readLine());
                // any process
                command = "DATA\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.getMultiline(this.input));
                // Describing the email SUBJECT and BODY
                command = "Subject:" + subject + "\r\n" + body + "\n.\r\n";
                System.out.println("CLIENT: " + command);
                this.output.writeBytes(command);
                System.out.println("SERVER: " + this.input.readLine());
                return true;
            } catch (IOException e) {
                System.out.println("ERROR: El servidor no esta respondiendo...");
                e.printStackTrace();
                return false;
            }
        } else {
            System.out.println("la conexion no esta establecida correctamente");
            return false;
        }
    }

    /**
     * log out of the smtp service and disconnect from the server
     *
     * @return if closed connection success, or had problems
     */
    public boolean closeServiceSMTP() {
        try {
            // Exit SMTP service
            String command = "QUIT\r\n";
            System.out.println("CLIENT: " + command);
            this.output.writeBytes(command);
            System.out.println("SERVER: " + this.input.readLine());
            // close connection to smtp server
            this.output.close();
            this.input.close();
            this.clientSocket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR, problemas al cerrar el servicio SMTP");
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
            if (line.charAt(3) == ' ') {
                lines = lines + "\n" + line;
                // No more lines in the server response
                break;
            }
            // Add read line to the list of lines
            lines = lines + "\n" + line;
        }
        return lines;
    }

}
