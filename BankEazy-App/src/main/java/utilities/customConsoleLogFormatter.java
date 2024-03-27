package utilities;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class customConsoleLogFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		String message = formatMessage(record);
		if(record.getLevel() == Level.FINE) {
			return "\u001B[94m" + message + "\u001B[0m\n";	//light purple color for Fine
		}
		else if (record.getLevel() == Level.INFO) {
            return "\u001B[32m" + message + "\u001B[0m\n";  // Green color for INFO
        } else if (record.getLevel() == Level.WARNING) {
            return "\u001B[33m" + message + "\u001B[0m\n";  // Yellow color for WARNING
        } else if (record.getLevel() == Level.SEVERE) {
            return "\u001B[31m" + message + "\u001B[0m\n";  // Red color for SEVERE
        } else {
            return "\u001B[37m" + message + "\u001B[0m\n";  // White color for other log levels
        }
	}

}
