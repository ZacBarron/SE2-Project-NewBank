package newbank.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

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
    public String addAccount(Account account){
        try {
            ArrayList<Account> accounts = new ArrayList<>();
            File accountsFile = new File(ACCOUNTS_FILEPATH);
            ObjectMapper mapper = new ObjectMapper();

            if(!accountsFile.createNewFile()){
                accounts = mapper.readValue(accountsFile, new TypeReference<ArrayList<Account>>(){});
            }
            if(!accounts.stream().anyMatch(a -> a.getAccountName().equals(account.getAccountName()))){
                accounts.add(account);
                mapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
            } else {
                return String.format("FAIL. Customer: %s already has an account with the name: %s",
                        account.getCustomerName(), account.getAccountName());
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return String.format("SUCCESS. %s account created for user: %s",
                account.getAccountName(), account.getCustomerName());
    }

    /*
    Read accounts information for a customer.
     */
    public String readAccounts(String customerName){
        List<Account> accounts = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File accountsFile = new File(ACCOUNTS_FILEPATH);
            accounts = mapper.readValue(accountsFile, new TypeReference<ArrayList<Account>>(){});
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        accounts =  accounts.stream().filter(a -> a.getCustomerName().equals(customerName))
                .collect(Collectors.toList());

        String s = "";
        for(Account a : accounts) {
            s += a.toString() + "\n";
        }
        s = s.substring(0,s.length()-1);
        return s;
    }

    public void updateAccount(){
        // To do
    }

}
