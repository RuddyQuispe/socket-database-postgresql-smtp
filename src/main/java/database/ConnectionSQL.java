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
    private String connectionURL;
    private Connection connectionDB;

    /**
     * @param host:     database server host
     * @param port:     port enable
     * @param username: user database owner or assigned
     * @param passwd:   password user database
     * @constructor Initialize the instance connection to database
     */
    public ConnectionSQL(String host, int port, String database, String username, String passwd) {
        // url JDBC drive connection
        this.connectionURL = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        // establishing the connection to the database
        try {
            this.connectionDB = DriverManager.getConnection(this.connectionURL, username, passwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.print("Error in establishing session PSQL");
        }
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
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