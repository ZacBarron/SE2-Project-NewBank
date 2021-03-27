package newbank.server;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerService {

    private static final int MAX_LENGTH = 20;
    private static final String PASSWORD_PATTERN = String.format(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$", MAX_LENGTH);

    public synchronized CustomerID checkLogInDetails(HashMap<String,Customer> customers, String userName, String password) {
        if(customers.containsKey(userName) && customers.get(userName).passwordCorrect(password)) {
            return new CustomerID(userName);
        }
        return null;
    }

    public synchronized boolean newUserNameIsValid(HashMap<String,Customer> customers, String userName) {
        if (customers.containsKey(userName) || userName.length() < 4) {
            return false;
        }
        return true;
    }

    public synchronized boolean newPasswordIsValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
