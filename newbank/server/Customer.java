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

	public Account getAccount(String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				return account;
			}
		}
		return null;
	}

	public boolean eligibleToPay(double amount, String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName) && account.getCurrentBalance() >= amount) {
				return true;
			}
		}
		return false;
	}

	public void modifyAccountBalance(double amount, String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				account.setCurrentBalance(amount);
			}
		}
	}
}
