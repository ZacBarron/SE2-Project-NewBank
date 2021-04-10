package newbank.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class DataService {
    private static final String ACCOUNTS_FILEPATH = "Accounts.json";
    private static final String CUSTOMERS_FILEPATH = "Customers.json";

    /*
    For some reason, the password field isn't carried across to the json
     */
    public void createUser(Customer customer) {
        try {
            File customers = new File(CUSTOMERS_FILEPATH);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customer);
            if(customers.createNewFile()){
                Files.write(customers.toPath(), Arrays.asList(json), StandardOpenOption.CREATE);
            }
            else{
                Files.write(customers.toPath(), Arrays.asList(json), StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /*
    So eventually we want to be in a position to use something like:
    Customer = mapper.readValue(json, Customer.class);
    on line 40, but currently this brings an error that we can't construct a customer that way
    We may need to change the way the Customer object works?
    THis will need a return type, either an arraylist of all customers or a single customer requested in the argument
     */
    public void readUsers() {
        ArrayList<String> customerList = new ArrayList<String>();
        try {
            ObjectMapper mapper = new ObjectMapper();

            File customers = new File(CUSTOMERS_FILEPATH);
            Scanner reader = new Scanner(customers);
            while(reader.hasNextLine()){
                String json = reader.nextLine();
                customerList.add(json);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println(customerList);
    }

    public void updateUser(Customer customer){
        // To do
    }

    /*
    Appends an account to the accounts file storage, or creates a new file if not exists.
     */
    public void addAccount(Account account){
        try {
            File accountsFile = new File(ACCOUNTS_FILEPATH);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(account);
            if(accountsFile.createNewFile()){
                Files.write(accountsFile.toPath(), Arrays.asList(json), StandardOpenOption.CREATE);
            }
            else{
                Files.write(accountsFile.toPath(), Arrays.asList(json), StandardOpenOption.APPEND);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public  void readAccounts(){
        ArrayList<String> accounts = new ArrayList<String>();
        try {
            ObjectMapper mapper = new ObjectMapper();

            File accountsFile = new File(ACCOUNTS_FILEPATH);
            Scanner reader = new Scanner(accountsFile);
            while(reader.hasNextLine()){
                String json = reader.nextLine();
                accounts.add(json);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println(accounts);
    }

    public void updateAccount(){
        // To do
    }

}
