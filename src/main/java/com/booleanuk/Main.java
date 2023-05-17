package com.booleanuk;
import com.booleanuk.api.Customer;
import com.booleanuk.api.CustomerRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CustomerRepository myRepo = null;
        try {
            myRepo = new CustomerRepository();
        } catch (Exception e) {
            System.out.println("Oops: " + e);
        }
        Scanner input = new Scanner(System.in);
        String userChoice = "";
        while (!userChoice.equals("X")) {
            System.out.println("\nPlease choose from the following options.\n");
            System.out.println("A Show all customers");
            System.out.println("B Show a specific customer");
            System.out.println("C Update a specific customer");
            System.out.println("D Delete a specific customer");
            System.out.println("E Add a new customer");
            System.out.println("X to exit the program");
            userChoice = input.nextLine().toUpperCase();
            if (userChoice.equals("A")) {
                try {
                    for (Customer customer : myRepo.getAll())
                        System.out.println(customer);
                } catch (Exception e) {
                    System.out.println("Oops: " + e);
                }
            } else if (userChoice.equals("B")) {
                System.out.println("\nPlease enter the id of the user you wish to view.");
                String value = input.nextLine();
                long id = Long.parseLong(value);
                try {
                    Customer customer = myRepo.get(id);
                    if (customer != null) {
                        System.out.println(customer);
                    } else {
                        System.out.println("Sorry that customer id was not recognised");
                    }
                } catch (Exception e) {
                    System.out.println("Oops: " + e);
                }
            } else if (userChoice.equals("C")) {
                System.out.println("\nPlease enter the id of the user you wish to view.");
                String value = input.nextLine();
                long id = Long.parseLong(value);
                System.out.println("Please enter the new name for the updated customer");
                String name = input.nextLine();
                System.out.println("Please enter the new address for the updated customer");
                String address = input.nextLine();
                System.out.println("Please enter the new email for the updated customer");
                String email = input.nextLine();
                System.out.println("Please enter the new phone number for the updated customer");
                String phone = input.nextLine();
                Customer updatedCustomer = new Customer(id, name, address, email, phone);
                try {
                    Customer theCustomer = myRepo.update(id, updatedCustomer);
                    if (theCustomer != null) {
                        System.out.println(theCustomer);
                    } else {
                        System.out.println("Sorry that customer id was not recognised");
                    }
                } catch (Exception e) {
                    System.out.println("Oops: " + e);
                }
            } else if (userChoice.equals("D")) {
                System.out.println("\nPlease enter the id of the user you wish to delete.");
                String theValue = input.nextLine();
                long theId = Long.parseLong(theValue);
                try {
                    Customer aCustomer = myRepo.delete(theId);
                    if (aCustomer != null) {
                        System.out.println(aCustomer);
                    } else {
                        System.out.println("Sorry that customer id was not recognised");
                    }
                } catch (Exception e) {
                    System.out.println("Oops: " + e);
                }
            } else if (userChoice.equals("E")) {
                System.out.println("Please enter the name for the new customer");
                String name = input.nextLine();
                System.out.println("Please enter the address for the new customer");
                String address = input.nextLine();
                System.out.println("Please enter the email for the new customer");
                String email = input.nextLine();
                System.out.println("Please enter the phone number for the new customer");
                String phone = input.nextLine();
                Customer customer = new Customer(0, name, address, email, phone);
                try {
                    customer = myRepo.add(customer);
                    if (customer != null) {
                        System.out.println(customer);
                    } else {
                        System.out.println("Sorry we couldn't add that customer for some reason");
                    }
                } catch (Exception e) {
                    System.out.println("Oops: " + e);
                }
            }
        }
    }
}
