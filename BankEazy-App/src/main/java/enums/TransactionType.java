package enums;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {
	DEPOSIT(0), WITHDRAW(1), DEBIT(2), CREDIT(3);
	private int type;
	private static Map<Integer, TransactionType> constantMap = new HashMap<>();
	
	static {
		for(TransactionType currType : TransactionType.values()) {
			constantMap.put(currType.getType(), currType);
		}
	}
	
	private TransactionType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public static TransactionType getTransactionType(int type) {
		return constantMap.get(type);
	}
	
}
