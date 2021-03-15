package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	
	public Customer() {
		accounts = new ArrayList<>();
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	/**
	 * This method is used to check if the customer already has an account with the given name
	 * @param accountName the name to be checked in the existing accounts
	 * @return boolean
	 */
	public boolean accountExists(String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to get the given account for a customer by accountName
	 * @param accountName the name of the account
	 * @return Account
	 */
	public Account getAccount(String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				return account;
			}
		}
		return null;
	}

	/**
	 * This method is used to check if the customer has sufficient funds
	 * on a given account for a transaction
	 * @param amount the amount to be transferred
	 * @param accountName the account to be checked
	 * @return boolean
	 */
	public boolean eligibleToPay(double amount, String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName) && account.getCurrentBalance() >= amount) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to modify the a given balance by a given amount
	 * @param amount the amount to be transferred
	 * @param accountName the account to pay from
	 * @return boolean
	 */
	public void modifyAccountBalance(double amount, String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				account.changeBalance(amount);
			}
		}
	}
}
