package utilities;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class customLogFileFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		return "\n" + record.getThreadID() + " | " + record.getMillis() + " | " + record.getLoggerName() + " | " + record.getSourceClassName() + " | " + record.getSourceMethodName() + "\n" + record.getMessage() + "\n";
	}

}
