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
    private MessageService messageService;

    DataService() {
        accountsFile = new File(ACCOUNTS_FILEPATH);
        mapper = new ObjectMapper();
        messageService = new MessageService();
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

    public ArrayList readUsers() {
        ArrayList<Customer> customerList = new ArrayList<Customer>();
        try {
            ObjectMapper mapper = new ObjectMapper();

            File customers = new File(CUSTOMERS_FILEPATH);
            Scanner reader = new Scanner(customers);
            while(reader.hasNextLine()){
                String json = reader.nextLine();
                Customer customer = mapper.readValue(json, Customer.class);
                customerList.add(customer);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return customerList;
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
                return messageService.accountAlreadyExist(account.getCustomerName(), account.getAccountName());
            }
        } catch (Exception e) {
            return messageService.unexpectedError(e);
        }
        return messageService.accountCreated(account.getAccountName(), account.getCustomerName());
    }

    /*
    Get the accounts of a customer by customer name.
     */
    public List<Account> getAccounts(String customerName){
        List<Account> accounts = new ArrayList<>();
        try {
            accounts = getAccountsFromFile();
        } catch (Exception e) {
            System.out.println(messageService.unexpectedError(e));
        }
        return accounts.stream().filter(a -> a.getCustomerName().equals(customerName))
                .collect(Collectors.toList());
    }

    public String updateAccount(Account account){
        List<Account> accounts;
        try {
            accounts = getAccountsFromFile();
            Account accountToUpdate = getSpecificAccount(accounts, account);
            if (accountToUpdate == null) {
                return messageService.noAccountFoundError(account.getCustomerName(), account.getAccountName());
            }
            int index = accounts.indexOf(accountToUpdate);
            accounts.set(index, account);
            mapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (Exception e) {
            return messageService.unexpectedError(e);
        }
        return messageService.accountUpdated(account.getAccountName());
    }

    public String deleteAccount(Account account){
        List<Account> accounts;
        try {
            accounts = getAccountsFromFile();
            Account accountToDelete = getSpecificAccount(accounts, account);
            if (accountToDelete == null) {
                return messageService.noAccountFoundError(account.getCustomerName(), account.getAccountName());
            }
            int index = accounts.indexOf(accountToDelete);
            accounts.remove(index);
            mapper.writerWithDefaultPrettyPrinter().writeValue(accountsFile, accounts);
        } catch (Exception e) {
            return messageService.unexpectedError(e);
        }
        return messageService.accountDeleted(account.getAccountName());
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
