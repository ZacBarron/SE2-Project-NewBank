package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			CustomerID customer = null;
			while(customer == null){
				// choose between sign in & sign up
				out.println("Sign in by pressing 0(zero) or sign up be pressing 1!");
				String startAction = in.readLine();

				customer = identifyCustomer(startAction);

				// if the user is authenticated then get requests from the user and process them
				if(customer == null){
					out.println("Log In Failed, please try again");
				}
			}
			out.println("Log In Successful. What do you want to do?");
			while(true) {
				String request = in.readLine();
				System.out.println("Request from " + customer.getKey());
				String response = bank.processRequest(customer, request);
				out.println(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	private CustomerID identifyCustomer(String startAction) throws IOException {
		if(startAction.equals("0")) {
			return signInUser();
		} else if (startAction.equals("1")) {
			return signUpUser();
		}
		return null;
	}

	private CustomerID signInUser() throws IOException {
		try {
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();

			// ask for password
			out.println("Enter Password");
			String password = in.readLine();

			out.println("Checking Details...");
			// authenticate user and get customer ID token from bank for use in subsequent requests
			return bank.checkLogInDetails(userName, password);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private CustomerID signUpUser() throws IOException {
		try {

			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			out.println("Verifying username...");
			if (!bank.newUserNameIsValid(userName)) {
				out.println("Invalid username. Username must be unique" +
						" and at least four characters long.");
				return signUpUser();
			}

			// ask for password
			out.println("Enter Password (minimum six characters long, " +
					"at least one digit, at least one lower and one upper case letter)");
			String password = in.readLine();
			out.println("Verifying password...");
			if (!bank.newPasswordIsValid(password)) {
				out.println("Invalid password. Please provide a password of minimum six characters," +
						" at least one numeric and one capitalized letter");
				return signUpUser();
			}

			out.println("Creating account...");
			return bank.createNewCustomerID(userName, password);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
