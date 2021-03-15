package newbank.server;

public class Account {
	
	private String accountName;

	/**
	 * @return the name of the account
	 */
	public String getAccountName() {
		return accountName;
	}

	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
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
