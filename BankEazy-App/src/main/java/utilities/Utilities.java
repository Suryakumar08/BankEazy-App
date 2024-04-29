package utilities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import exception.CustomBankException;

public class Utilities {
	
	
	public static long getDateInMillis(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli();
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}
	
	public static String getDateString(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		String resultDate = date.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
		return resultDate;
	}
	
	public static String getDOBString(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime date = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		String resultDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return resultDate;
	}
	
	public static String getDateTimeString(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		String resultDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy - HH-mm-ss"));
		return resultDateTime;
	}
	
	public static Integer getInteger(Object obj, String message) throws CustomBankException{
		Validators.checkNull(obj, "Empty input");
		Integer result = null;
		try {
			if(obj instanceof String) {
				result = Integer.parseInt((String)obj);
			}
			else {
				result = (int)obj;
			}
			return result;
		}catch(Exception ex) {
			throw new CustomBankException("Invalid number input!" + message, ex);
		}
	}
	
	public static Long getLong(Object obj, String message) throws CustomBankException{
		Validators.checkNull(obj, "Empty input");
		Long result = null;
		try {
			if(obj instanceof String) {
				result = Long.parseLong((String)obj);
			}
			else {
				result = (long)obj;
			}
			return result;
		}catch(Exception ex) {
			throw new CustomBankException("Invalid number input!" + message, ex);
		}
	}
	
	public static Double getDouble(Object obj, String message) throws CustomBankException{
		Validators.checkNull(obj, "Empty input");
		Double result = null;
		try {
			if(obj instanceof String) {
				result = Double.parseDouble((String)obj);
			}
			else {
				result = (double)obj;
			}
			return result;
		}catch(Exception ex) {
			throw new CustomBankException("Invalid number input!" + message, ex);
		}
	}
	
	
	public static String sanitizeString(String input, String message) throws CustomBankException{
		Validators.checkNull(input, message);
		 return input.replaceAll("&", "&amp;")
	                .replaceAll("<", "&lt;")
	                .replaceAll(">", "&gt;")
	                .replaceAll("\"", "&quot;")
	                .replaceAll("'", "&#x60;");
	}
	
}

