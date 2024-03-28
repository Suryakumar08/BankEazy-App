package utilities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
	
	public static String getDateTimeString(long millis) {
		Instant instant = Instant.ofEpochMilli(millis);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		String resultDateTime = dateTime.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy - HH-mm-ss"));
		return resultDateTime;
	}
}

