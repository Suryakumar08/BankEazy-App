package enums;

import java.util.HashMap;
import java.util.Map;

public enum TransactionStatus {
	SUCCESS(1), FAILURE(0);
	private int status;
	private static Map<Integer, TransactionStatus> constantMap = new HashMap<>();
	
	static {
		for(TransactionStatus currStatus : TransactionStatus.values()) {
			constantMap.put(currStatus.getStatus(), currStatus);
		}
	}
	
	public static TransactionStatus getTransactionStatus(int status) {
		return constantMap.get(status);
	}
	
	private TransactionStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}
}
