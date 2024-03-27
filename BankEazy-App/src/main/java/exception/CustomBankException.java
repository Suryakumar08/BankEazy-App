package exception;

public class CustomBankException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final String NULL_MESSAGE = "Null Encountered! No value found!!!";
	public static final String EMPTY_INPUT = "Empty Input";
	public static final String GIVE_PROPER_INPUT = "Please, Provide Proper Input!";
	public static final String ERROR_OCCURRED = "ERROR OCCURRED! Sorry for the Inconvenience!!!";
	public static final String INVALID_PASSWORD = "Invalid password!";
	public static final String INVALID_MOBILE = "Invalid mobile number!";
	public static final String ACCOUNT_NOT_EXISTS = "Account Not Exists!";
	public static final String UPDATION_FAILED = "Updation failed! Try again later!!";
	public static final String USER_NOT_EXISTS = "User Not Exists!!!";
	public static final String NOT_ENOUGH_BALANCE = "Not enough Balance available for Transaction!!!";
	public static final String TRANSACTION_FAILED = "Transaction Failed!";
	public static final String INVALID_TRANSACTION = "Invalid Transaction";
	public static final String ACCOUNT_INACTIVE = "Account Inactive";
	public static final String INCORRECT_PASSWORD = "Incorrect password!";
	
	public CustomBankException(String message) {
		super(message);
	}
	
	public CustomBankException(String message, Throwable cause) {
		super(message, cause);
	}
}
