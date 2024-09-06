package com.booleanuk.api.customer;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CustomerRepository {
   private DataSource datasource;
    private String dbUser;
    private String dbURL;
    private String dbPassword;
    private String dbDatabase;
    private Connection connection;

    public CustomerRepository() throws SQLException {
        //get credentials
        this.getDatabaseCredentials();
        // set up the datasource
        this.datasource = this.createDataSource();
        // Set up connection
        this.connection = this.datasource.getConnection();
    }

    private void getDatabaseCredentials() {
        try(InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.dbUser = prop.getProperty("db.user");
            this.dbURL = prop.getProperty("db.url");
            this.dbPassword = prop.getProperty("db.password");
            this.dbDatabase = prop.getProperty("db.database");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private DataSource createDataSource() {
        final String url = "jdbc:postgresql://"+ this.dbURL
                + ":5432/" + this.dbDatabase
                + "?user=" + this.dbUser
                + "&password=" + this.dbPassword;
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        return dataSource;
    }

    public void connectToDatabase() throws SQLException {
        PreparedStatement statement = this.connection.
                prepareStatement("SELECT * FROM Customers");

        ResultSet results = statement.executeQuery();
//        while(results.next()) {
//            String id = "" + results.getInt("id");
//            String name = results.getString("name");
//            String address = results.getString("address");
//            System.out.println(id + " - " + name + " - " + address + " - ");
//        }

    }
    public List<Customer> getAll () throws SQLException {
        List<Customer> everyOne = new ArrayList<>();
        PreparedStatement statement = this.connection.
                prepareStatement("SELECT * FROM Customers");
        ResultSet results = statement.executeQuery();
        while(results.next()) {
            Customer customer = new Customer(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("address"),
                    results.getString("email"),
                    results.getString("phone"));
            everyOne.add(customer);
        }
        return everyOne;
    }

    public Customer getOne (int id)  throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Customers WHERE id= ?");
        statement.setInt(1, id);

        ResultSet results = statement.executeQuery();
        Customer customer = null;
         if(results.next()) {
             customer = new Customer(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("address"),
                    results.getString("email"),
                    results.getString("phone"));
             return customer;
        }
        return customer;
    }

    public Customer update(int id, Customer customer) throws SQLException {
        String SQL = "UPDATE Customers "
                + "SET name= ?, "
                + "address= ?, "
                + "email= ?, "
                + "phone= ?, "
                + "WHERE id+? ";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setString(1, customer.getName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getEmail());
        statement.setString(4, customer.getPhoneNumber());
        statement.setInt(5, id);
        int rowsAffected = statement.executeUpdate();
        Customer updatedCustomer = null;
        if(rowsAffected > 0) {
            updatedCustomer = this.getOne(id);
        }
        return updatedCustomer;
    }

    public Customer delete(int id) throws SQLException{
        String SQL = "DELETE FROM Customer WHERE id=?";
        PreparedStatement statement = this.connection.prepareStatement(SQL);
        statement.setInt(1,id);
        Customer deletedCustomer = null;
        deletedCustomer = this.getOne(id);
        int rowsAffected = statement.executeUpdate();
        if(rowsAffected == 0 ) {
            deletedCustomer = null;
        }
        return deletedCustomer;
    }

    public Customer add(Customer customer) throws SQLException {
        String SQL = "INSERT INTO Customers (name, address, email, phone) VALUES (?,?,?,?)";
        PreparedStatement statement = this.connection.prepareStatement(SQL,
                Statement.RETURN_GENERATED_KEYS);
        statement.setString(1,customer.getName());
        statement.setString(2,customer.getAddress());
        statement.setString(3,customer.getEmail());
        statement.setString(4,customer.getPhoneNumber());
        int rowsAffected = statement.executeUpdate();
        int newID = 0;
        if(rowsAffected > 0) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if(rs.next()) {
                    newID = rs.getInt(1);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            customer.setId(newID);
        } else {
            customer = null;
        }
        return customer;


    }

}
