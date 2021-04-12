package newbank.server;

import java.util.List;

public class MessageService {

    public String commandNotRecognized() {
        return "FAIL. Command not recognized.";
    }

    public String noAccount() {
        return "You have no accounts, use the NEWACCOUNT command to create an account";
    }

    public String newAccountFormatError() {
        return "FAIL. This command requires the following format: NEWACCOUNT <Name>";
    }

    public String moveFormatError() {
        return "FAIL. This command requires the following format: MOVE <Amount> <From> <To>";
    }

    public String amountNotNumericError() {
        return "FAIL. Please enter a numeric value for argument: <Amount>";
    }

    public String printAccounts(List<Account> accounts) {
        String s = "";
        for(Account a : accounts) {
            s += a.toString() + "\n";
        }
        s = s.substring(0,s.length()-1);
        return s;
    }

    public String sourceOrDestinationAccountNotExist(String account, String accountName) {
        return String.format("FAIL. The %s account %s does not exist", account, accountName);
    }

    public String sourceAndDestinationIsSameError() {
        return "FAIL. The source and destination accounts must be different";
    }

    public String insufficientFundsFail(String account) {
        return String.format("FAIL. Insufficient funds in account: %s", account);
    }

    public String moveSuccess(String amount, String source, String destination) {
        return String.format("SUCCESS. %s has been moved from %s to %s", amount, source, destination);
    }

    public String payFormatError() {
        return "FAIL. This command requires the following format: PAY <Person/Company> <Amount> <From> <To>";
    }

    public String payeeNotExistError(String payee) {
        return String.format("FAIL. Payee: %s does not exist", payee);
    }

    public String noAccountFoundError(String customer, String payerAccount) {
        return String.format("FAIL. Customer: %s has no account with the name: %s",
                customer, payerAccount);
    }

    public String paySuccess(String amount, String customer, String account) {
        return String.format("SUCCESS. %s payed for user: %s from account: %s", amount, customer, account);
    }

    public String payExternalFormatError() {
        return "FAIL. This command requires the following format: PAY <Amount> <From> <Sort Code> <Account Number>";
    }

    public String invalidDestinationSortCode() {
        return "FAIL. Invalid sort code for destination account";
    }

    public String invalidDestinationAccountNumber() {
        return "FAIL. Invalid account number for destination account";
    }

    public String payExternalSuccess(String amount, String accountNum, String payerAccount) {
        return String.format("SUCCESS. %s paid to account number %s paid from account: %s", amount, accountNum, payerAccount);
    }

    public String changePasswordFormatError() {
        return "FAIL. This command requires the following format: CHANGEPASSWORD  <current password> <new password> <retype new password>>";
    }

    public String incorrectPasswordError() {
        return "FAIL. The current password is incorrect";
    }

    public String passwordsDontMatch() {
        return "FAIL. The new password and retyped new password do not match";
    }

    public String passwordComplexityError() {
        return "FAIL. The new password does not meet complexity requirements. " +
                "It must contain at least one numeric character, " +
                "one uppercase letter and one lowercase letter and be at least six characters";
    }

    public String changePasswordSuccess() {
        return "SUCCESS. The password has been updated";
    }

    public String helpFormatError() {
        return "FAIL. This command requires the following format: HELP or HELP <Command>";
    }

    public String unexpectedError(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(e.getStackTrace());
        return "An unexpected error occurred. Please try again later or contact customer support.";
    }

    public String accountUpdated(String account) {
        return String.format("SUCCESS. %s account updated", account);
    }

    public String accountDeleted(String account) {
        return String.format("SUCCESS. %s account deleted", account);
    }

    public String accountAlreadyExist(String customerName, String accountName) {
        return String.format("FAIL. Customer: %s already has an account with the name: %s",
                customerName, accountName);
    }

    public String accountCreated(String accountName, String customerName) {
        return String.format("SUCCESS. %s account created for user: %s",
                accountName, customerName);
    }
}
