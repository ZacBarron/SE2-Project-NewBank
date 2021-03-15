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

	/**
	 * @return the current balance
	 */
	public double getCurrentBalance() {
		return currentBalance;
	}

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.currentBalance = openingBalance;
	}

	// Change the current balance by the given amount
	public void changeBalance(double amount) {
		currentBalance += amount;
	}

	public String toString() {
		return (accountName + ": " + currentBalance);
	}

}
