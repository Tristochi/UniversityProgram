package admin;

public enum AccountTypes {
	STUDENT(1, "Student"), PROFESSOR(2, "Professor");

	private final int key;
	private final String value;

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
}
