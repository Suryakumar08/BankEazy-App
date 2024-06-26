package utilities;

import java.util.regex.Pattern;

import exception.CustomBankException;

public class Validators {
	public static void checkNull(Object input) throws CustomBankException{
		if(input == null) {
			throw new CustomBankException(CustomBankException.NULL_MESSAGE);
		}
	}
	
	public static void validateInput(CharSequence input) throws CustomBankException{
		checkNull(input);
		if(input.equals("")) {
			throw new CustomBankException(CustomBankException.EMPTY_INPUT);
		}
	}
	
	public static void validatePassword(String password, String message) throws CustomBankException{
		validateInput(password);
		if(!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[<>@#$%^&+=!]).{8,}$", password)) {
			throw new CustomBankException(message);
		}
	}
	
	
	public static void validatePassword(String password) throws CustomBankException{
		validateInput(password);
		if(!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[<>@#$%^&+=!]).{8,}$", password)) {
			throw new CustomBankException(CustomBankException.INVALID_PASSWORD);
		}
	}
	
	public static void validateMobile(String mobile) throws CustomBankException{
		validateInput(mobile);
		if(!Pattern.matches("^[6-9]\\d{9}\n?$", mobile)) {
			throw new CustomBankException(CustomBankException.INVALID_MOBILE);
		}
	}

	public static void checkNull(Object object, String message) throws CustomBankException{
		if(object == null) {
			throw new CustomBankException(message);
		}
	}

	public static void checkRangeBound(double value, double min, double max, String message) throws CustomBankException{
		if(value < min || value > max) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED + " " + message);
		}
		
	}
	
	public static void checkRangeBound(int value, int min, int max, String message) throws CustomBankException{
		if(value < min || value > max) {
			throw new CustomBankException(CustomBankException.ERROR_OCCURRED + " " + message);
		}
	}
	
	public static void validateNotNull(String value, String fieldName) throws CustomBankException {
	    if (value == null || value.isEmpty()) {
	        throw new CustomBankException(fieldName + " cannot be empty!");
	    }
	}


	public static void validateDob(long dobInMillis) throws CustomBankException {
	    long eighteenYearsInMillis = 18L * 365 * 24 * 60 * 60 * 1000;
	    long currentTimeInMillis = System.currentTimeMillis();
	    long minDobInMillis = currentTimeInMillis - eighteenYearsInMillis;
	    if (dobInMillis > minDobInMillis) {
	        throw new CustomBankException("Invalid date of birth! Age should be at least 18 years.");
	    }
        long millisecondsIn70Years = 70L * 365L * 24L * 60L * 60L * 1000L;
        long time70YearsAgoMillis = currentTimeInMillis - millisecondsIn70Years;
	    if(dobInMillis < time70YearsAgoMillis) {
	    	throw new CustomBankException("Age should be below 70 years");
	    }
	}

	public static void validateAadhar(String aadhar) throws CustomBankException {
	    if (aadhar == null || !aadhar.matches("\\d{12}")) {
	        throw new CustomBankException("Invalid Aadhar number! Aadhar number should be exactly 12 digits.");
	    }
	}

	public static void validatePan(String pan) throws CustomBankException {
	    if (pan == null || !pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")) {
	        throw new CustomBankException("Invalid PAN number! PAN number should have the format: ABCDE1234F");
	    }
	}

	public static void validateIsLong(String string, String message) throws CustomBankException{
		try {
			Long.parseLong(string);
		}catch(Exception ex) {
			throw new CustomBankException(message);
		}
	}
	
	public static void validateIsInteger(String number, String message) throws CustomBankException{
		try {
			Integer.parseInt(number);
		}
		catch(Exception ex) {
			throw new CustomBankException(message);
		}
	}

	public static void validateIFSC(String ifsc) throws CustomBankException{
		checkNull(ifsc, "Empty IFSC!");	
		if(!ifsc.matches("^[A-Z]{4}0[0-9]{6}")) {
			throw new CustomBankException("Invalid IFSC!!!");
		}
	}
	
}
