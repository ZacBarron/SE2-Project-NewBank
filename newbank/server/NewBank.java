package newbank.server;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private HashMap<String,Help> helpCommands;
	private CustomerService customerService;
	private DataService dataService;
	private MessageService messageService;
	
	private NewBank() {
		customers = new HashMap<>();
		helpCommands = new HashMap<>();
		addHelpCommands();
		customerService = new CustomerService();
		dataService = new DataService();
		addTestData();
		dataService.readUsers();
		messageService = new MessageService();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer("Bhagy", "Foooo1");
		bhagy.addAccount(new Account("Main", 1000.0, "Bhagy"));
		bhagy.addAccount(new Account("Savings", 1500.0, "Bhagy"));
		// testing adding accounts to the persistence layer
		dataService.createAccount(new Account("Main", 1000.0, "Bhagy"));
		dataService.createAccount(new Account("Savings", 1500.0, "Bhagy"));
		customers.put("Bhagy", bhagy);
		dataService.createUser(bhagy);
		
		Customer christina = new Customer("Christina", "Foooo2");
		christina.addAccount(new Account("Savings", 1500.0, "Christina"));
		customers.put("Christina", christina);
		
		Customer john = new Customer("John", "Foooo3");
		john.addAccount(new Account("Checking", 250.0, "John"));
		customers.put("John", john);
	}

	private void addHelpCommands() {
		Help help = new Help("HELP or HELP <Command>", "e.g. HELP MOVE", "Returns a help description for all commands or for one specific command");
		helpCommands.put("HELP", help);

		Help showMyAccounts = new Help("SHOWMYACCOUNTS", "", "Returns a list of all the customers accounts along with their current balance");
		helpCommands.put("SHOWMYACCOUNTS", showMyAccounts);

		Help newAccount = new Help("NEWACCOUNT <Name>", "e.g. NEWACCOUNT Savings", "Returns SUCCESS or FAIL");
		helpCommands.put("NEWACCOUNT", newAccount);

		Help move = new Help("MOVE <Amount> <From> <To>", "e.g. MOVE 100 Main Savings ", "Returns SUCCESS or FAIL");
		helpCommands.put("MOVE", move);

		Help pay = new Help("PAY <Person/Company> <Amount> <From> <To>", "e.g. PAY John 100 Main Savings", "Returns SUCCESS or FAIL");
		helpCommands.put("PAY", pay);

		Help payExternal = new Help("PAYEXTERNAL <Amount> <From> <Sort Code> <Account Number>", "PAYEXTERNAL 100 102030 12345678", "Returns SUCCESS or FAIL");
		helpCommands.put("PAYEXTERNAL", payExternal);

		Help changePassword = new Help("CHANGEPASSWORD <current password> <new password> <retype new password>", "e.g CHANGEPASSWORD password123 p@55w.rd1234 p@55w.rd1234","Returns SUCCESS or FAIL");
		helpCommands.put("CHANGEPASSWORD", changePassword);

		Help logOut = new Help("LOGOUT", "","Returns Log out");
		helpCommands.put("LOGOUT", logOut);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		return customerService.checkLogInDetails(customers, userName, password);
	}

	// creates a new customer from the given credentials
	public synchronized CustomerID createNewCustomerID(String userName, String password, LocalDate dob) {
		Customer newCustomer = new Customer(userName, password);
		newCustomer.setDOB(dob);
		customers.put(userName, newCustomer);
		dataService.createUser(newCustomer);
		return new CustomerID(userName);
	}

	public synchronized boolean newUserNameIsValid(String userName) {
		return customerService.newUserNameIsValid(customers, userName);
	}

	public synchronized boolean newPasswordIsValid(String password) {
		return customerService.newPasswordIsValid(password);
	}

	public synchronized boolean isOverEighteen(LocalDate dob) {
		return customerService.isOverEighteen(dob);
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
			case "PAYEXTERNAL" : return payExternal(customer, commandLine);
			case "HELP" : return help(commandLine);
			case "CHANGEPASSWORD" : return changePassword(customer, commandLine);
			case "LOGOUT" : return logOut(customer);
			default : return messageService.commandNotRecognized();
			}
		}
		return messageService.commandNotRecognized();
	}

	private String showMyAccounts(CustomerID customer) {
		// Fail if the incorrect number of arguments are passed
		if(!customers.get(customer.getKey()).hasAnAccount()) {
			return messageService.noAccount();
		}

		// Get the requester
		Customer customerEntity = customers.get(customer.getKey());

		List<Account> accounts = dataService.getAccounts(customerEntity.getUserName());

		return messageService.printAccounts(accounts);
	}

	private String createNewAccount(CustomerID customerID, String[] commandLine) {
		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 2) {
			return messageService.newAccountFormatError();
		}

		String accountName = commandLine[1];
		return dataService.createAccount(new Account(accountName, 0, customer.getUserName()));
	}

	private String move(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 4) {
			return messageService.moveFormatError();
		}

		// Fail if the amount argument is non-numeric
		try {
			double amount = Double.parseDouble(commandLine[1]);
		} catch (NumberFormatException nfe) {
			return messageService.amountNotNumericError();
		}

		// Get the requester
		Customer customer = customers.get(customerID.getKey());

		// Fail if the from and to accounts don't exist
		if(!customer.accountExists(commandLine[2])) {
			return messageService.sourceOrDestinationAccountNotExist("source", commandLine[2]);
		}
		if(!customer.accountExists(commandLine[3])) {
			return messageService.sourceOrDestinationAccountNotExist("destination", commandLine[3]);
		}

		// Fail if source and destination accounts are the same
		if(commandLine[2].equals(commandLine[3])) {
			messageService.sourceAndDestinationIsSameError();
		}

		// Set source and destination accounts
		Account sourceAccount = customer.getAccount(commandLine[2]);
		Account destinationAccount = customer.getAccount(commandLine[3]);

		// Fail if source account has insufficient funds
		double amount = Double.parseDouble(commandLine[1]);
		if (sourceAccount.getCurrentBalance() < amount) {
			return messageService.insufficientFundsFail(commandLine[2]);
		}

		// Decrease source account balance
		sourceAccount.changeBalance(amount * -1);
		// Increase destination account balance
		destinationAccount.changeBalance(amount);
		return messageService.moveSuccess(commandLine[1], commandLine[2], commandLine[3]);
	}

	private String pay(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 5) {
			return messageService.payFormatError();
		}

		double amount;
		// Fail if the amount argument is non-numeric
		try {
			amount = Double.parseDouble(commandLine[2]);
		} catch (NumberFormatException nfe) {
			return messageService.amountNotNumericError();
		}

		Customer payer = customers.get(customerID.getKey());
		Customer payee = customers.get(commandLine[1]);
		String payerAccount = commandLine[3];
		String payeeAccount = commandLine[4];


		// FAIL if the recipient not exists
		if (payee == null) {
			return messageService.payeeNotExistError(commandLine[1]);
		}

		// FAIL if the payer account not exists
		if (!payer.accountExists(payerAccount)) {
			return messageService.noAccountFoundError(customerID.getKey(), payerAccount);
		}

		// FAIL if the payee account not exists
		if (!payee.accountExists(payeeAccount)) {
			return messageService.noAccountFoundError(commandLine[1], payeeAccount);
		}

		// FAIL if the payer account has insufficient funds
		if (!payer.eligibleToPay(amount, payerAccount)) {
			return messageService.insufficientFundsFail(payerAccount);
		}

		// Modify balances
		payer.modifyAccountBalance(amount * -1, payerAccount);
		payee.modifyAccountBalance(amount, payeeAccount);

		return messageService.paySuccess(commandLine[2], commandLine[1], payerAccount);
	}


	private String payExternal(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if (commandLine.length != 5) {
			return messageService.payExternalFormatError();
		}
		double amount;

		// Fail if the amount argument is non-numeric
		try {
			amount = Double.parseDouble(commandLine[1]);
		} catch (NumberFormatException nfe) {
			return messageService.amountNotNumericError();
		}

		Customer payer = customers.get(customerID.getKey());
		String payerAccount = commandLine[2];

		// FAIL if the payer account not exists
		if (!payer.accountExists(payerAccount)) {
			return messageService.noAccountFoundError(customerID.getKey(), payerAccount);
		}

		// FAIL if the payer account has insufficient funds
		if (!payer.eligibleToPay(amount, payerAccount)) {
			return messageService.insufficientFundsFail(payerAccount);
		}

		// FAIL if sort code invalid format
		if (!commandLine[3].matches(".*\\d.*") && commandLine[3].length() != 6) {
			return messageService.invalidDestinationSortCode();
		}

		// FAIL if account number invalid format
		if (!commandLine[4].matches(".*\\d.*") && commandLine[4].length() != 8) {
			return messageService.invalidDestinationAccountNumber();
		}

		// Modify balance
		// Additional logic required to handle real external payments in the production version of the client
		payer.modifyAccountBalance(amount * -1, payerAccount);
		return messageService.payExternalSuccess(commandLine[1], commandLine[4], payerAccount);
	}

	private String changePassword(CustomerID customerID, String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if(commandLine.length != 4) {
			return messageService.changePasswordFormatError();
		}
		Customer customer = customers.get(customerID.getKey());
		// Fail if current password is incorrect
		if(!customer.passwordCorrect(commandLine[1])) {
			return messageService.incorrectPasswordError();
		}
		// Fail if new password and retype new password don't match
		if(!commandLine[2].equals(commandLine[3])) {
			return messageService.passwordsDontMatch();
		}
		// Fail if new password does not meet the complexity requirements
		if(!bank.newPasswordIsValid(commandLine[2])) {
			return messageService.passwordComplexityError();
		}
		customer.setPassword(commandLine[2]);
		return messageService.changePasswordSuccess();
	}

	private String help(String[] commandLine) {
		// Fail if the incorrect number of arguments are passed
		if (commandLine.length > 2)
			return messageService.helpFormatError();

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
		return messageService.commandNotRecognized();
	}

	private String logOut(CustomerID customer) {
		return "Log out";
	}
}
