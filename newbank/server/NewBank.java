package newbank.server;

import java.util.HashMap;
import java.util.Map.Entry;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private HashMap<String,Help> helpCommands;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
		helpCommands = new HashMap<>();
		addHelpCommands();
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

	private void addHelpCommands() {
		Help help = new Help("HELP or HELP <Command>", "e.g. HELP MOVE", "Returns a help description for all commands or for one specific command");
		helpCommands.put("HELP", help);

		Help showMyAccounts = new Help("SHOWMYACCOUNTS", "e.g. Main: 1000.0", "Returns a list of all the customers accounts along with their current balance");
		helpCommands.put("SHOWMYACCOUNTS", showMyAccounts);

		Help newAccount = new Help("NEWACCOUNT <Name>", "e.g. NEWACCOUNT Savings", "Returns SUCCESS or FAIL");
		helpCommands.put("NEWACCOUNT", newAccount);

		Help move = new Help("MOVE <Amount> <From> <To>", "e.g. MOVE 100 Main Savings ", "Returns SUCCESS or FAIL");
		helpCommands.put("MOVE", move);

		Help pay = new Help("PAY <Person/Company> <Amount> <From> <To>", "e.g. PAY John 100 Main Savings", "Returns SUCCESS or FAIL");
		helpCommands.put("PAY", pay);

		Help changePassword = new Help("CHANGEPASSWORD <current password> <new password> <retype new password>", "e.g CHANGEPASSWORD password123 p@55w.rd1234 p@55w.rd1234","Returns SUCCESS or FAIL");
		helpCommands.put("CHANGEPASSWORD", changePassword);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName) && customers.get(userName).passwordCorrect(password)) {
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
			case "NEWACCOUNT" : return createNewAccount(customer, commandLine);
			case "MOVE" : return move(customer, commandLine);
			case "PAY" : return pay(customer, commandLine);
			case "HELP" : return help(commandLine);
			case "CHANGEPASSWORD" : return changePassword(customer, commandLine);
			default : return "FAIL. Command not recognized.";
			}
		}
		return "FAIL. Customer not recognized.";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String createNewAccount(CustomerID customerID, String[] commandLine) {
		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 2) {
			return "FAIL. This command requires the following format: NEWACCOUNT <Name>";
		}

		String accountName = commandLine[1];
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
			return "FAIL. This command requires the following format: MOVE <Amount> <From> <To>";
		}

		// Fail if the amount argument is non-numeric
		try {
			double amount = Double.parseDouble(commandLine[1]);
		} catch (NumberFormatException nfe) {
			return "FAIL. This command requires the following format: MOVE <Amount> <From> <To>";
		}

		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Fail if the from and to accounts don't exist
		if(!customer.accountExists(commandLine[2])) {
			return String.format("FAIL. The source account %s does not exist", commandLine[2]);
		}
		if(!customer.accountExists(commandLine[3])) {
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
		if (sourceAccount.getCurrentBalance() < amount) {
			return "FAIL. Insufficient funds for the transfer";
		}

		// Decrease source account balance
		sourceAccount.changeBalance(amount * -1);
		// Increase destination account balance
		destinationAccount.changeBalance(amount);
		return String.format("SUCCESS. %s has been moved from %s to %s", commandLine[1], commandLine[2], commandLine[3]);
	}

	private String pay(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 5) {
			return "FAIL. This command requires the following format: PAY <Person/Company> <Amount> <From> <To>";
		}

		double amount;
		// Fail if the amount argument is non-numeric
		try {
			amount = Double.parseDouble(commandLine[2]);
		} catch (NumberFormatException nfe) {
			return "FAIL. Please enter a numeric value for argument: <Amount>";
		}

		Customer payer = customers.get(customerID.getKey());
		Customer payee = customers.get(commandLine[1]);
		String payerAccount = commandLine[3];
		String payeeAccount = commandLine[4];


		// FAIL if the recipient not exists
		if (payee == null) {
			return String.format("FAIL. Payee: %s does not exist", commandLine[1]);
		}

		// FAIL if the payer account not exists
		if (!payer.accountExists(payerAccount)) {
			return String.format("FAIL. Customer: %s has no account with the name: %s",
					customerID.getKey(), payerAccount);
		}

		// FAIL if the payee account not exists
		if (!payee.accountExists(payeeAccount)) {
			return String.format("FAIL. Customer: %s has no account with the name: %s",
					commandLine[1], payeeAccount);
		}

		// FAIL if the payer account has insufficient funds
		if (!payer.eligibleToPay(amount, payerAccount)) {
			return String.format("FAIL. Insufficient funds in account: %s", payerAccount);
		}

		// Modify balances
		payer.modifyAccountBalance(amount * -1, payerAccount);
		payee.modifyAccountBalance(amount, payeeAccount);

		return String.format("SUCCESS. %s payed for user: %s from account: %s", amount, commandLine[1], payerAccount);
	}

	private String changePassword(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 4) {
			return "FAIL. This command requires the following format: CHANGEPASSWORD  <current password> <new password> <retype new password>>";
		}
		Customer customer = customers.get(customerID.getKey());
		// Fail if current password is incorrect
		if(!customer.passwordCorrect(commandLine[1])) {
			return "FAIL. The current password is incorrect";
		}
		// Fail if new password and retype new password don't match
		if(!commandLine[2].equals(commandLine[3])) {
			return "FAIL. The new password and retyped new password do not match";
		}
		// Fail if new password does not meet the complexity requirements
		if(!customer.meetsComplexityRequirements(commandLine[2])) {
			return "FAIL. The new password does not meet complexity requirements. It must contain at least one numeric character, one uppercase letter and one lowercase letter";
		}
		customer.setPassword(commandLine[2]);
		return "SUCCESS. The password has been updated";
	}

	private String help(String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if (commandLine.length > 2)
			return "FAIL. This command requires the following format: HELP or HELP <Command>";

		// Print all commands help if only HELP is typed
		if (commandLine.length == 1) {
			String allHelpCommands = "";
			for (Entry<String, Help> entry : helpCommands.entrySet()) {
					Help helpCommand = entry.getValue();
					allHelpCommands += helpCommand.toString() + "\n\n";
			}
			return allHelpCommands;
		}

		// Print specific help for the specific command entered after HEL
		for (Entry<String, Help> entry : helpCommands.entrySet()) {
			if (entry.getKey().equals(commandLine[1])) {
				Help helpCommand = entry.getValue();
				return helpCommand.toString();
			}
		}
		return "FAIL. The command you entered does not exist";
	}
}
