package newbank.server;

import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		// Split into into separate words to handle multi-word commands
		String[] commandLine = request.split(" ");
		String command = commandLine[0];

		if(customers.containsKey(customer.getKey())) {
			switch(command) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "NEWACCOUNT" : return createNewAccount(customer, commandLine[1]);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String createNewAccount(CustomerID customerID, String accountName) {
		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Return FAIL if the requester already has an account with the given name
		if (customer.alreadyHasAnAccountWithName(accountName)) {
			return String.format("FAIL. Customer: %s already has an account with the name: %s", customerID.getKey(), accountName);
		}

		// Create the account
		customer.addAccount(new Account(accountName, 0));

		return String.format("SUCCESS. %s account created for user: %s", accountName, customerID.getKey());
	}
}
