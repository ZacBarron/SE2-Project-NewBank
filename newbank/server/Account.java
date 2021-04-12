package newbank.server;
import java.util.Random;

public class Account {
	
	private String accountName;
	private String sortCode;
	private String accountNumber;
	private String customerName;

	/**
	 * @return the name of the account
	 */
	public String getAccountName() {
		return accountName;
	}

	private double currentBalance;

	/**
	 * @return the current balance
	 */
	public double getCurrentBalance() {
		return currentBalance;
	}

	/**
	 * @return the sort code
	 */
	public String getSortCode() {
		return sortCode;
	}

	/**
	 * @return the account number
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return the name of the related customer
	 */
	public String getCustomerName() {
		return customerName;
	}

	public Account(String accountName, double openingBalance, String customerName) {
		this.accountName = accountName;
		this.currentBalance = openingBalance;
		this.sortCode = "102030";
		this.accountNumber = generateAccountNumber(8);
		this.customerName = customerName;
	}

	public Account() { }

	// Change the current balance by the given amount
	public void changeBalance(double amount) {
		currentBalance += amount;
	}

	public String toString() {
		String balanceTo2SF = String.format("%.2f", this.currentBalance);
		String accountDetails = accountName;
		accountDetails += ("\nAccount number: " + accountNumber);
		accountDetails += ("\nSort code: " + sortCode);
		accountDetails += ("\nBalance: " + balanceTo2SF + "\n");

		return accountDetails;
	}

	private String generateAccountNumber(int length) {
		Random rand = new Random();
		String accountNumber = "";
		for(int i=0; i<length; i++) {
			accountNumber += String.valueOf(rand.nextInt(10));
		}
		return accountNumber;
	}

}
