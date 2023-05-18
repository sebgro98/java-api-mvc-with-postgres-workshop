# Java API Using MVC with a Postgres Database Workshop

## Learning Objectives
- Use the MVC pattern with Spring Boot to communicate with a Postgres database
- Create an API which has a database behind it
- Use Database Migrations and Java classes to create and model the database

## Instructions

1. Set up a new project in IntelliJ 
2. Make sure the `flyway` config is pointing to the correct database and local folders.
3. Make sure the folder to hold the database migrations is correctly created.
4. Run `flyway -cleanDisabled=false clean` at the command line to drop all of the tables currently in the database. 
5. Create the first migration file to define a new Customer Table that matches the following:

| Customer |              |          |
|----------|--------------|----------|
| ID       | Serial       | PK       |
| Name     | VarChar(250) | NOT NULL |
| Address  | VarChar(500) | NOT NULL |
| Email    | VarChar(200) | NOT NULL |
| Phone    | VarChar(30)  |          |

6. Then add a second migration script which can be run after the first one and will populate the table with some initial dummy data.

| ID | Name              | Address                                               | Email                        | Phone     |
|----|-------------------|-------------------------------------------------------|------------------------------|-----------|
| *  | Ada Lovelace      | Church of St Mary Magdalene, Hucknall, Nottingham, UK | ada@lovelace.com             | 012345675 |
| *  | Charles Babbage   | Kensal Green, London, UK                              | charles@differenceengine.com | 012345674 |
| *  | Grace Hopper      | Arlington County, Virginia, USA                       | grace@bugsrus.com            | 012345673 |
| *  | Alan Turing       | 43 Adlington Road, Wilmslow, Cheshire, UK             | alan@bletchleypark.org.uk    | 012345672 |
| *  | Katherine Johnson | Newport News, Virginia, USA                           | katherine@nasa.org           | 012345671 |

Don't put a value for the ID when inserting the data as that field will be auto-generated.

7. Run the migrations and then use TablePlus to check the table and data are in the database and that any previous tables have been dropped.

## Adding the Database Connection to the Java Project

1. Go to [https://mvnrepository.com/artifact/org.postgresql/postgresql](https://mvnrepository.com/artifact/org.postgresql/postgresql) and click on the latest driver.

2. In the new page find the tab marked: `Gradle (Short)` and click on it.

3. Then copy the code that is in the window.

4. Paste the code you just copied into the build.gradle file in the project, add it to the dependencies section so that it looks like this:

```groovy
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	// https://mvnrepository.com/artifact/org.postgresql/postgresql
	implementation 'org.postgresql:postgresql:42.6.0'
}
```

5. Click on the icon that appears at the top right of the window to reload the gradle project, or click on the Gradle tab and select reload from there to trigger gradle to update the dependencies.

6. Assuming everything runs smoothly then you should be ready to proceed to the next step.

## Making a Connection to the Database

1. Make a new package inside the com.booleanuk.api package called customer (which will give us the com.booleanuk.api.customer package path).

2. Then inside that new package folder create 3 new Java classes Customer.java, CustomerController.java and CustomerRepository.java.

3. The first of these will be a class which will become instances of entries in the Database, so when we read an entry into our code, we will create a Customer instance and populate its fields with the corresponding data.

4. The CustomerController class is going to handle the Spring Boot side of things and will deal with the endpoints to data requests. It will do this by using the data from the API request to call for corresponding data from teh CustomerRepository class which will in turn get this data from the database.

5. As stated before the CustomerRepository will have methods which will access data from the database and return the appropriate values/objects depending upon the query received.

6. Let's start by testing out if we can reach the database from our CustomerRepository class.

**WARNING: MAKE SURE YOU ADD THE FOLLOWING TO .GITIGNORE OTHERWISE YOUR PERSONAL LOGIN TO ELEPHANTSQL WILL BE SHARED WITH THE WORLD!!!!!!!!**

7. Open the `.gitignore` file and add the following lines to the bottom of it:

```
### Personal Settings ###
*.properties
report.html.html
report.html.json
```

this will make git ignore any files anywhere in your Project that end with `.properties` (it also adds two files to be ignored that running flyway generates).

8. In the `resources` folder inside the src->main folder create a new file called `config.properties`. We're going to add our ElephantSQL connection details in here so that we can then read them into our code, without actually sharing them with anyone else. Open the new file and add the following to it:

```
db.url=<Value for Server (without brackets) from ElephantSQL Details page>
db.user=<Value for User & Default database from ElephantSQL Details page>
db.password=<Value for Password from ElephantSQL Details page>
db.database=<Value for User & Default database from ElephantSQL Details page>
```

replace the items in angle brackets with the details from your ElephantSQL account (you need to remove the angle brackets too).

9. Open the CustomerRepository class and add the following code to make Java read in the details from the properties file we created, populate some fields with those values, create a connection to the database and then read all of the Customers table data into a variable called results which we then iterate through displaying the results as we go.

10. Make a `main()` method in a `Main` class if it doesn't already exist and change the code so that it contains the following:

```java
package com.booleanuk;
import com.booleanuk.api.CustomerRepository;

public class Main {
    public static void main(String[] args) {
        CustomerRepository myRepo = new CustomerRepository();
        try {
            myRepo.connectToDatabase();
        }
        catch(Exception e) {
            System.out.println("Oops: " + e);
        }
    }
}
```

11. This tries to create the database connection and output the contents of the table. If you are successful then you should see something like the following:

```

> Task :Main.main()
1 - Ada Lovelace - Church of St Mary Magdalene, Hucknall, Nottingham, UK
2 - Charles Babbage - Kensal Green, London, UK
3 - Grace Hopper - Arlington County, Virginia, USA
4 - Alan Turing - 43 Adlington Road, Wilmslow, Cheshire, UK
5 - Katherine Johnson - Newport News, Virginia, USA

BUILD SUCCESSFUL in 979ms
3 actionable tasks: 2 executed, 1 up-to-date
13:28:14: Execution finished ':Main.main()'.

```

12. If you added different data to the table then you will see that instead of this.

At this point you might be thinking that you should just read the data from the table into an ArrayList of Customer objects, populated with the contents of the table. If you do that though, then you will have to write all of the Java code to search for and manage Customers. It is a much better strategy to allow the database to do these things as databases are designed for precisely this use case.

## Setting up the Customer Class

Now that we can connect to the database, we're going to make create our `Customer` class so that we can use it to store actual data from the database.

1. Look at the structure of the `Customer` table it has an id which is a number (a long in this case) and then all of the other fields will be Strings, so in the Customer class add fields to match those values, and a constructor to assign them. This should be fairly straightforward to do:

```java
package com.booleanuk.api;

public class Customer {
    private long id;
    private String name;
    private String address;
    private String email;
    private String phoneNumber;
    
    public Customer(long id, String name, String address, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
```

2. Now thinking back to the MVC exercise we did previously and relating it to this we will have paths to `Create a Customer`, `Get all Customers`, `Get a specific Customer`, `Update a Customer` and `Delete a Customer`. When searching for individual customers we will do so using their ID. So in the CustomerRepository class we are going to want to make methods to do each of these things which will return either a `Customer` object, or a `List<Customer>` of them. So if we replicate those methods but this time use our new connection to the database to get the data then we should be ready to connect them all to SpringBoot to make it all happen.

3. Let's start by adding a `toString()` method to the `Customer` class to make it easier for us to debug what we are doing. It doesn't need to output everything, let's make it just output a string containing the id, name and address for now.

```java
    public String toString() {
        String result = "";
        result += this.id + " - ";
        result += this.name + " - ";
        result += this.address;
        return result;
    }
```

4. Now go into the `CustomerRepository` class and create a new method that throws an `SQLException` called `getAll` as follows

```java
    public List<Customer> getAll() throws SQLException  {
        List<Customer> everyone = new ArrayList<>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM CUSTOMERS");

        ResultSet results = statement.executeQuery();

        while (results.next()) {
            Customer theCustomer = new Customer(results.getLong("id"), results.getString("name"), results.getString("address"), results.getString("email"), results.getString("phone"));
            everyone.add(theCustomer);
        }
        return everyone;
    }
```

5. To call it from `main()` we'll need to check for exceptions too. So change the `Main` class to be something like this:

```java
package com.booleanuk;
import com.booleanuk.api.Customer;
import com.booleanuk.api.CustomerRepository;

public class Main {
    public static void main(String[] args) {
        try {
            CustomerRepository myRepo = new CustomerRepository();
        }
        catch(Exception e) {
            System.out.println("Oops: " + e);
        }
        try {
            for (Customer customer : myRepo.getAll())
             System.out.println(customer);
        }
        catch(Exception e) {
            System.out.println("Oops: " + e);
        }
    }
}
```

6. Which should print out the database contents nicely.

7. Next we want to write a method to get a single `Customer` back when we access it via its `id`. Firstly let's do this in a way that isn't how we should approach this, as it can leave the site open to SQL Injection attacks or similar. 

![Little Bobby Tables](https://imgs.xkcd.com/comics/exploits_of_a_mom.png). 

8. Do not do it this way.

```java
    public Customer get(long id) throws SQLException {
        // This is bad!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        PreparedStatement statement = this.connection.prepareStatement("SELECT * FROM Customers WHERE id=" + id);
        ResultSet results = statement.executeQuery();
        Customer customer = null;
        if (results.next()) {
            customer = new Customer(results.getLong("id"), results.getString("name"), results.getString("address"), results.getString("email"), results.getString("phone"));
        }
        return customer;
    }
```

9. In the previous code, malicious users could potentially supply something that caused the SQL to misbehave (in this case they probably can't because the value of `id` is specified as a `long` and is an argument to the method, if we allowed id to be passed as a String then this wouldn't be the case and we could have serious problems as a result), to avoid this we can do the following instead. Below is a better way to do it where we use ? as a place holder for the variable we pass in and then parse it into the String afterwards. In theory this should escape any incoming SQL and prevent malicious users from injecting their own SQL into the program.

```java
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
```

10. The line `statement.setLong(1, id);` is where we parse the value of `id` into the SQL that is then executed. The `1` represents the first placeholder in the SQL statement that is replaced with the value of the argument (I don't know why it's not zero-indexed either).

11. We are not limited to using `setLong` to replace placeholder values we can also use a variety of other techniques (use the autocomplete facility in Intellij to see what's available). Next we're going to look at how we can update a customer's details, the code will return the updated Customer from the method or a null Customer if it didn't find the id.

```java
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
```

12. Here we're using `SetString` to replace the placeholders where necessary. The `executeUpdate` method returns an integer of the number of rows updated by the code and when we do our API we're going to want to return the updated user object, so we just check if the rows affected is greater than 0 and use the `get` method to get the Customer we have jsut been working on to return.

13. There will need to be changes to the `Main` class as well in order to make this work. 

14. Now that we have methods to get all of the Customers, get an individual Customer by their id and update a Customer based on their id, we want to add a method to allow us to Delete a customer from the database, thinking about what happened previously with the APIs we used, we want it to return the deleted Customer object if we successfully delete them and a null Customer if we don't.

```java
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
```

15. You also need to add a method that will allow you to create a new Customer, based on details supplied, when we create the new customer object that gets passed to the `add` method it doesn't matter what id value it has, as the `add` method just retrieves the data from the other fields in order to use it to insert a new customer and the id field gets generated by the database automatically. As it does this and we may want to know the id of the new Customer we get the `Insert` command to also return the value of the new id that has been generated. Once we have that we retrieve the data from the database and pass it back to the user.

```java
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
```

16. One thing you may find is that as we're working on the database adding and removing entries we end up with very few rows left in the table, and these are still the same rows that are left when we come back to use the database later. We can use our Flyway migrations to fix this whilst we are in Development by running them as we did previously. There are ways to trigger migrations from inside our Java program (we can define the migrations themselves inside Java classes) but we'll leave that as something you may want to investigate individually. The activities we will do tomorrow inhabit a similar domain so it may be that once you have completed those, you will no longer want to use Flyway to do this. Use `flyway -cleanDisabled=false clean`, `flyway info` and `flyway migrate` as we did previously to recreate the test database.

## Using Spring Boot with the CustomerRepository and Customer Classes

Using the Customer Repository we can follow the same process we used when creating an in-memory SpringBoot application to create endpoints that communicate with the database via an API. The code for this is in the CustomerController file which we'll talk through now.

There is a specification for the Customer and Stock APIs here: [API Doc](https://boolean-uk.github.io/java-api-mvc-with-postgres-workshop/)

## Exercise

Once you have a working Customer API, add in StockItem, StockRepository and StockController classes and implement the API for Stock, you will need to create the relevant table in ElephantSQL and query it from your Java code in exactly the same way we did with the Customers. The API descriptions are here: [API Doc](https://boolean-uk.github.io/java-api-mvc-with-postgres-workshop/)

Once you have the classes working locally add the SpringBoot code to the StockController class to make everything work.














