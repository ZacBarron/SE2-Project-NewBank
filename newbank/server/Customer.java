package newbank.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import java.time.LocalDate;

public class Customer {

	private ArrayList<Account> accounts;
	private String userName;
	private String forenames;
	private String surname;
	private String password;
	private String phoneNumber;
	private String emailAddress;
	private String dob;
	private PostalAddress address;

	public Customer() {
		super();
	}

	public Customer(String userName, String password) {
		accounts = new ArrayList<>();
		this.userName = userName;
		this.password = password;
	}
	
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

	public void setDOB(String dateOfBirth) {dob = dateOfBirth;}

	public void setPostalAddress(PostalAddress postalAddress) {address = postalAddress;}

	// Getter methods for personal details
	public String getUserName() {return userName;}

	public String getForenames() {return forenames;}

	public String getSurname() {return surname;}

	public String getPassword() {return password;}

	public String getPhoneNumber() {return phoneNumber;}

	public String getEmailAddress() {return emailAddress;}

	public String getDOB() {return dob;}

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

	public boolean hasAnAccount() {
		if (this.accounts.isEmpty()) {
			return false;
		}
		return true;
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
	 * This method checks if an input password is correct
	 */
	public boolean passwordCorrect(String pw) {
		if(pw.equals(this.password)){
			return true;
		}
		return false;
	}

	/**
	 * This method is used to modify the a given balance by a given amount
	 * @param amount the amount to be transferred
	 * @param accountName the account to pay from
	 */
	public void modifyAccountBalance(double amount, String accountName) {
		for (Account account : accounts) {
			if (account.getAccountName().equals(accountName)) {
				account.changeBalance(amount);
			}
		}
	}
}
