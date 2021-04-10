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

    private File accountsFile;
    private ObjectMapper mapper ;

    DataService() {
        accountsFile = new File(ACCOUNTS_FILEPATH);
        mapper = new ObjectMapper();
    }

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
    public String createAccount(Account account){
        try {
            ArrayList<Account> accounts = new ArrayList<>();

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
    Get the accounts of a customer by customer name.
     */
    public List<Account> getAccounts(String customerName){
        List<Account> accounts = new ArrayList<>();
        try {
            accounts = getAccountsFromFile();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return accounts.stream().filter(a -> a.getCustomerName().equals(customerName))
                .collect(Collectors.toList());
    }

    public void updateAccount(Account account){
        List<Account> accounts;
        try {
            accounts = getAccountsFromFile();
            Account accountToUpdate = getSpecificAccount(accounts, account);

            int index = accounts.indexOf(accountToUpdate);
            accounts.set(index, account);
            mapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void deleteAccount(Account account){
        List<Account> accounts;
        try {
            accounts = getAccountsFromFile();
            Account accountToDelete = getSpecificAccount(accounts, account);

            int index = accounts.indexOf(accountToDelete);
            accounts.remove(index);
            mapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private List<Account> getAccountsFromFile() throws Exception {
        try {
            return mapper.readValue(accountsFile, new TypeReference<ArrayList<Account>>(){});
        } catch (Exception e) {
            throw new Exception("Failed to get accounts");
        }
    }

    private Account getSpecificAccount(List<Account> accounts, Account specificAccount) {
        return accounts.stream()
                .filter(a -> a.getCustomerName().equals(specificAccount.getCustomerName())
                        && a.getAccountName().equals(specificAccount.getAccountName()))
                .findFirst().get();
    }

}
