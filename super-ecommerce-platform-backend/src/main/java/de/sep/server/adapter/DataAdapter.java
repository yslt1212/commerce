package de.sep.server.adapter;

import de.sep.server.util.Logger;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.util.concurrent.Semaphore;

@Getter
@Setter
public class DataAdapter {

    protected static Connection connection = null;
    String table;
    String nameCol;
    String idCol;
    Logger logger = new Logger(getClass().getCanonicalName());

    public DataAdapter(String table) throws SQLException {
        createConnection();
        this.table = table;
        this.idCol = table + "_id";
        this.nameCol = table + "name";
    }

    private void createConnection() throws SQLException {
        // DriverManager: The basic service for managing a set of JDBC drivers.
        if(DataAdapter.connection == null){
            System.out.println("Connection Successful! Enjoy. Now it's time to push data");
            DataAdapter.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazonas", "root", "");
        }

        if (DataAdapter.connection == null) {
            System.out.println("Failed to make connection!");
        }

    }

    // Gets all and returns an ResultSet of them
    public ResultSet getAll() throws SQLException {
        // Have to init this as array cause else we cant set it in thread
        ResultSet[] rs = {null};

        String getQueryStatement = "SELECT * FROM " + table;

        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement);
        // Execute the Query, and get a java ResultSet
        rs[0] = preparedStatement.executeQuery();

        return rs[0];
    }







    // Gets all and returns an ResultSet of them
    public ResultSet getById(int id) throws SQLException {
        // Have to init this as array cause else we cant set it in thread
        ResultSet[] rs = {null};

        String getQueryStatement = "SELECT * FROM " + table + " where " + idCol + " = " + id;
        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement);
        // Execute the Query, and get a java ResultSet
        rs[0] = preparedStatement.executeQuery();


        return rs[0];
    }
    // Search for an element with given name
    // exactly determines if u want the exact name or only parts of the name
    public ResultSet getByName(String name, Boolean exactly) throws SQLException {
        // Have to init this as array cause else we cant set it in thread
        ResultSet[] rs = {null};

        // Trying to fetch Data from the given table

        // Deleting empty Space escapes
        String nameWithoutSpace = name.replaceAll("%20", " ");
        // Using parts of name as basis
        String getQueryStatement =
                "SELECT * FROM "
                        + table
                        + " where "
                        + nameCol
                        + " like '%"
                        + nameWithoutSpace
                        + "%'";

        if (exactly) {
            getQueryStatement =
                    "SELECT * FROM "
                            + table
                            + " where "
                            + nameCol
                            + " like '"
                            + nameWithoutSpace
                            + "'";
        }

        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement);
        // Execute the Query, and get a java ResultSet
        rs[0] = preparedStatement.executeQuery();

        return rs[0];
    }

    public void deleteById(String id) throws SQLException{

        // Trying to fetch Data from the given table
        // Deleting empty Space escapes
        // Using parts of name as basis
        String getQueryStatement = "Delete FROM " + table + " where " + idCol + " = " + id;

        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement);

        // Execute the Query, and get a java ResultSet
        preparedStatement.executeUpdate();
    }

    public ResultSet getQuery(String query) throws SQLException {
        ResultSet[] rs = {null};

        // Using a lock to wait for the query to reset the ResultSet

        String getQueryStatement = query;

        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement);

        // Execute the Query, and get a java ResultSet
        rs[0] = preparedStatement.executeQuery();


        return rs[0];
    }

    // example query
    /*

    INSERT INTO user (username, password, email, address, balance, img_src, type)
    VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', '4006', 'Norway', '1');

        UPDATE user
    SET username = 'Alfred Schmidt', balance= '200'
    WHERE userid = 1;

     */
    public int addQuery(String query) throws SQLException, SQLIntegrityConstraintViolationException {
        String getQueryStatement = query;
        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();

        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        if(resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            return -1;
        }

    }

    public void updateQuery(String query) throws SQLException, SQLIntegrityConstraintViolationException {

        System.out.println(query);
        String getQueryStatement = query;
        PreparedStatement preparedStatement = connection.prepareStatement(getQueryStatement, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
    }
}
