package enums;

import java.util.HashMap;
import java.util.Map;

public enum UserStatus {
	ACTIVE(1),INACTIVE(0);
	private int status;
	private static Map<Integer, UserStatus> constantMap = new HashMap<>();
	
	
	static {
		for(UserStatus currStatus : UserStatus.values()) {
			constantMap.put(currStatus.getStatus(), currStatus);
		}
	}
	
	public static UserStatus getUserStatus(int status) {
		return constantMap.get(status);
	}
	
	private UserStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}
}
