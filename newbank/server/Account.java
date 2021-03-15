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
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

}
