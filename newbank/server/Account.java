package newbank.server;

public class Account {
	
	private String accountName;

	/**
	 * @return the name of the account
	 */
	public String getAccountName() {
		return accountName;
	}

	private double currentBalance;

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double amount) {
		currentBalance += amount;
	}

	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
		this.currentBalance = openingBalance;
	}

	// Return the account balance
	public double getBalance() {
		return openingBalance;
	}

	public double changeBalance(double amount) {
		openingBalance += amount;
		return openingBalance;
	}

	public String toString() {
		return (accountName + ": " + openingBalance);
	}

}
