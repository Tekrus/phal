package enums;

public enum UnaryOperator{
	NOT,
	NEGATIVE;

	@Override
	public String toString() {
		String stringRep = "";
		switch (this) {
			case NOT:
				stringRep = "!";
				break;
			case NEGATIVE:
				stringRep = "-";
				break;
			default:
				stringRep = "error";
				break;
		}
		return stringRep;
	}
}
