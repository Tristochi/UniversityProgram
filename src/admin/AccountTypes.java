package admin;

import java.util.HashMap;
import java.util.Map;

public enum AccountTypes {
	STUDENT(1, "Student"), PROFESSOR(2, "Professor");

	private final int key;
	private final String value;
	
	private static Map<Integer, AccountTypes> map = new HashMap<Integer, AccountTypes>();
	
	static {
		for(AccountTypes account : AccountTypes.values()) {
			map.put(account.key, account);
		}
	}

	AccountTypes(int key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public static AccountTypes valueOf(int key) {
		return map.get(key);
	}
}
