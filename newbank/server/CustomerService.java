package newbank.server;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerService {

    private static final int MAX_LENGTH = 20;
    private static final String PASSWORD_PATTERN = String.format(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$", MAX_LENGTH);

    private static final String USERNAME_PATTERN = String.format(
            "^[a-zA-Z0-9\\-_]{4,}$", MAX_LENGTH);

    public CustomerID checkLogInDetails(HashMap<String,Customer> customers, String userName, String password) {
        if(customers.containsKey(userName) && customers.get(userName).passwordCorrect(password)) {
            return new CustomerID(userName);
        }
        return null;
    }

    public boolean newUserNameIsValid(HashMap<String,Customer> customers, String userName) {
        if (customers.containsKey(userName) || !validByRegexPattern(USERNAME_PATTERN, userName)) {
            return false;
        }
        return true;
    }

    public boolean newPasswordIsValid(String password) {
        return validByRegexPattern(PASSWORD_PATTERN, password);
    }

    private boolean validByRegexPattern(String regexPattern, String stringToValidate) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(stringToValidate);
        return matcher.matches();
    }
}
