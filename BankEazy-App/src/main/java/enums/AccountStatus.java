package enums;

import java.util.HashMap;
import java.util.Map;

public enum AccountStatus {
	ACTIVE(1), INACTIVE(0);

	private int status;
	private static Map<Integer, AccountStatus> constantMap = new HashMap<>();

	static {
		for (AccountStatus currStatus : AccountStatus.values()) {
			constantMap.put(currStatus.getStatus(), currStatus);
		}
	}

	public static AccountStatus getAccountStatus(int status) {
		return constantMap.get(status);
	}

	private AccountStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return this.status;
	}

}
