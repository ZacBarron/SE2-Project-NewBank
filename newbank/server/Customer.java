package newbank.server;

import java.util.ArrayList;

import java.time.LocalDate;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String forenames;
	private String surname;
	private String password;
	private String phoneNumber;
	private String emailAddress;
	private LocalDate dob;
	private PostalAddress address;
	
	public Customer() {accounts = new ArrayList<>();}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString() + "\n";
		}
		s = s.substring(0,s.length()-1);
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}

	// Setter methods for personal details
	public void setForenames(String names) {forenames = names;}

	public void setSurname(String name) {surname = name;}

	public void setPassword(String pass) {password = pass;}

	public void setPhoneNumber(String num) {phoneNumber = num;}

	public void setEmail(String address) {emailAddress = address;}

	public void setDOB(int year, int month, int day) {dob.of(year, month, day);}

	public void setPostalAddress(PostalAddress postalAddress) {address = postalAddress;}

	// Getter methods for personal details
	public String getForenames() {return forenames;}

	public String getSurname() {return surname;}

	public String getPhoneNumber() {return phoneNumber;}

	public String getEmailAddress() {return emailAddress;}

	public LocalDate getDOB() {return dob;}

	public PostalAddress getAddress() {return address;}

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
