package com.booleanuk.api;

import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class CustomerRepository {
    DataSource datasource;
    String dbUser;
    String dbURL;
    String dbPassword;
    String dbDatabase;
    Connection connection;

    public CustomerRepository() throws SQLException  {
        this.getDatabaseCredentials();
        this.datasource = this.createDataSource();
        this.connection = this.datasource.getConnection();
    }

    private void getDatabaseCredentials() {
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        } catch(Exception e) {
            System.out.println("Oops: " + e);
        }
    }

    private DataSource createDataSource() {
        // The url specifies the address of our database along with username and password credentials
        // you should replace these with your own username and password
        final String url = "jdbc:postgresql://" + this.dbURL + ":5432/" + this.dbDatabase + "?user=" + this.dbUser +"&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(url);
        return dataSource;
    }



    public List<Customer> getAll() throws SQLException  {
        List<Customer> everyone = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Customers");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Customer theCustomer = new Customer(results.getLong("id"), results.getString("name"), results.getString("address"), results.getString("email"), results.getString("phone"));
            everyone.add(theCustomer);
        }
        return everyone;
    }

    public Customer get(long id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Customers WHERE id = ?");
        // Choose set**** matching the datatype of the missing element
        statement.setLong(1, id);
        ResultSet results = statement.executeQuery();
        Customer customer = null;
        if (results.next()) {
            customer = new Customer(results.getLong("id"), results.getString("name"), results.getString("address"), results.getString("email"), results.getString("phone"));
        }
        return customer;
    }

    public Customer update(long id, Customer customer) throws SQLException {
        String SQL = "UPDATE Customers " +
                "SET name = ? ," +
                "address = ? ," +
                "email = ? ," +
                "phone = ? " +
                "WHERE id = ? ";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, customer.getName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getEmail());
        statement.setString(4, customer.getPhoneNumber());
        statement.setLong(5, id);
        int rowsAffected = statement.executeUpdate();
        Customer updatedCustomer = null;
        if (rowsAffected > 0) {
            updatedCustomer = this.get(id);
        }
        return updatedCustomer;
    }

    public Customer delete(long id) throws SQLException {
        String SQL = "DELETE FROM Customers WHERE id = ?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        // Get the customer we're deleting before we delete them
        Customer deletedCustomer = null;
        deletedCustomer = this.get(id);

        statement.setLong(1, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected == 0) {
            //Reset the customer we're deleting if we didn't delete them
            deletedCustomer = null;
        }
        return deletedCustomer;
    }

    public Customer add(Customer customer) throws SQLException {
        String SQL = "INSERT INTO Customers(name, address, email, phone) VALUES(?, ?, ?, ?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, customer.getName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getEmail());
        statement.setString(4, customer.getPhoneNumber());
        int rowsAffected = statement.executeUpdate();
        long newId = 0;
        if (rowsAffected > 0) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    newId = rs.getLong(1);
                }
            } catch (Exception e) {
                System.out.println("Oops: " + e);
            }
            customer.setId(newId);
        } else {
            customer = null;
        }
        return customer;
    }
}

