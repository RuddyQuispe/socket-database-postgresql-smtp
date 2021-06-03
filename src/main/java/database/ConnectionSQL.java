package database;

import java.sql.*;
import java.util.Properties;

/**
 * @class connection to database in postgreSQL (PSQL)
 */
public class ConnectionSQL {
    /**
     * Attributes
     */
    private String ConnectionURL;
    private Properties properties;
    private Connection connectionDB;

    /**
     * @param host:     database server host
     * @param port:     port enable
     * @param username: user database owner or assigned
     * @param passwd:   password user database
     * @constructor Initialize the instance connection to database
     */
    public ConnectionSQL(String host, int port, String username, String passwd) {
        // url jdbc drive connection
        this.ConnectionURL = "jdbc:postgresql://" + host + ":" + port + "/" + username;
        // save user session in properties
        this.properties = new Properties();
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", passwd);
        // establishing the connection to the database
        try {
            this.connectionDB = DriverManager.getConnection(this.ConnectionURL, this.properties);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.print("Error in establishing session PSQL");
        }
    }

    public String getConnectionURL() {
        return ConnectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        ConnectionURL = connectionURL;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Connection getConnectionDB() {
        return connectionDB;
    }

    public void setConnectionDB(Connection connectionDB) {
        this.connectionDB = connectionDB;
    }

    /**
     * execute query SQL to the database connected
     * throw a problem set time out or error in query sql
     *
     * @param sqlQuery: any sql query
     * @return object that contains the data produced by the given query; never null
     */
    public ResultSet executeQuerySQL(String sqlQuery) {
        try {
            Statement statement = this.connectionDB.createStatement();
            return statement.executeQuery(sqlQuery);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }
}