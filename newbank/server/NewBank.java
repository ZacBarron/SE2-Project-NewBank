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
		bhagy.addAccount(new Account("Savings", 1500.0));
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
			case "MOVE" : return move(customer, commandLine);
			case "PAY" : return executePayment(customer, commandLine);
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
		if (customer.accountExists(accountName)) {
			return String.format("FAIL. Customer: %s already has an account with the name: %s",
					customerID.getKey(), accountName);
		}

		// Create the account
		customer.addAccount(new Account(accountName, 0));

		return String.format("SUCCESS. %s account created for user: %s", accountName, customerID.getKey());
	}

	private String move(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 4) {
			return "FAIL. This command requires an amount, a source account, and a destination account";
		}

		// Fail if the amount argument is non-numeric
		try {
			double amount = Double.parseDouble(commandLine[1]);
		} catch (NumberFormatException nfe) {
			return "FAIL. This command requires an amount, a source account, and a destination account";
		}

		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Fail if the from and to accounts don't exist
		if(!customer.alreadyHasAnAccountWithName(commandLine[2])) {
			return String.format("FAIL. The source account %s does not exist", commandLine[2]);
		}
		if(!customer.alreadyHasAnAccountWithName(commandLine[3])) {
			return String.format("FAIL. The destination account %s does not exist", commandLine[3]);
		}

		// Fail if source and destination accounts are the same
		if(commandLine[2].equals(commandLine[3])) {
			return "FAIL. The source and destination accounts must be different";
		}

		// Set source and destination accounts
		Account sourceAccount = customer.getAccount(commandLine[2]);
		Account destinationAccount = customer.getAccount(commandLine[3]);

		// Fail if source account has insufficient funds
		double amount = Double.parseDouble(commandLine[1]);
		if (sourceAccount.getBalance() < amount) {
			return "FAIL. Insufficient funds for the transfer";
		}

		// Decrease source account balance
		sourceAccount.changeBalance(amount * -1);
		// Increase destination account balance
		destinationAccount.changeBalance(amount);
		return String.format("SUCCESS. %s has been moved from %s to %s", commandLine[1], commandLine[2], commandLine[3]);
	}

	private String executePayment(CustomerID customerID, String[] commandLine) {
		Customer payer = customers.get(customerID.getKey());
		Customer payee = customers.get(commandLine[1]);
		String accountName = commandLine[3];
		double amount = Double.parseDouble(commandLine[2]);

		if (payee == null) {
			return String.format("FAIL. Payee: %s not exists", commandLine[1]);
		}

		if (!payer.accountExists(accountName)) {
			return String.format("FAIL. Customer: %s has no account with the name: %s",
					customerID.getKey(), accountName);
		}

		if (!payer.eligibleToPay(amount, accountName)) {
			return String.format("FAIL. Insufficient funds on account: %s", accountName);
		}

		payer.modifyAccountBalance(-amount, accountName);
		payee.modifyAccountBalance(amount, "Main");

		return String.format("SUCCESS. %s payed for user: %s from account: ", amount, payee, accountName);
	}
}
