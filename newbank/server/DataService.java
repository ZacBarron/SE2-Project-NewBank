package newbank.server;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private File customersFile;
    private ObjectMapper mapper ;
    private MessageService messageService;

    DataService() {
        accountsFile = new File(ACCOUNTS_FILEPATH);
        customersFile = new File(CUSTOMERS_FILEPATH);
        mapper = new ObjectMapper();
        messageService = new MessageService();
    }

    public String createUser(Customer customer){
        try {
            List<Customer> customers = new ArrayList<>();

            if(!customersFile.createNewFile()){
                customers = mapper.readValue(customersFile, new TypeReference<ArrayList<Customer>>(){});
            }
            customers.add(customer);
            mapper.writerWithDefaultPrettyPrinter().writeValue(customersFile, customers);
        } catch (Exception e) {
            return messageService.unexpectedError(e);
        }
        return "SUCCESS";
    }

    public ArrayList<Customer> readUsers() {
        try {
            return mapper.readValue(customersFile, new TypeReference<ArrayList<Customer>>(){});
        } catch (Exception e) {
            return null;
        }
    }

    public String updateUser(Customer customer) throws JsonProcessingException {
        List<Customer> customers;
        try {
            customers = readUsers();
            Customer customerToUpdate = getSpecificCustomer(customers, customer);
            if (customerToUpdate == null) {
                return messageService.noCustomerFoundError(customer.getUserName());
            }
            int index = customers.indexOf(customerToUpdate);
            customers.set(index, customer);
            mapper.writerWithDefaultPrettyPrinter().writeValue(customersFile, customers);
        } catch (Exception e) {
            return messageService.unexpectedError(e);
        }
        return "OK";
    }

    /*
    Appends an account to the accounts file storage, or creates a new file if not exists.
     */
    public String createAccount(Account account){
        try {
            List<Account> accounts = new ArrayList<>();

            if(!accountsFile.createNewFile()){
                accounts = mapper.readValue(accountsFile, new TypeReference<ArrayList<Account>>(){});
            }
            List<Account> customerAccounts = getAccountsForUSer(accounts, account.getCustomerName());
            if(!customerAccounts.stream().anyMatch(a -> a.getAccountName().equals(account.getAccountName()))){
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
        System.out.println(accounts.stream().filter(a -> a.getCustomerName().equals(customerName))
                .collect(Collectors.toList()));
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

    private List<Account> getAccountsForUSer(List<Account> accounts, String customerName) {
        return accounts.stream().filter(a -> a.getCustomerName().equals(customerName))
                .collect(Collectors.toList());
    }

    private Customer getSpecificCustomer(List<Customer> customers, Customer specificCustomer) {
        return customers.stream()
                .filter(a -> a.getUserName().equals(specificCustomer.getUserName()))
                .findFirst().get();
    }
}
