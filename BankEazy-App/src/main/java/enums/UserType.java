package enums;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
	Customer(0), Employee(1), Admin(2);
	private int type;
	private static Map<Integer, UserType> constantMap = new HashMap<>();
	
	static {
		for(UserType currType : UserType.values()) {
			constantMap.put(currType.getType(), currType);
		}
	}
	
	private UserType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
	public static UserType getUserType(int type) {
		return constantMap.get(type);
	}
}
