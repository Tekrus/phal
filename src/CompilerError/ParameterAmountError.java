package CompilerError;

public class ParameterAmountError extends Error{
	int expectedAmount;
	int actualAmount;
	
	public ParameterAmountError(int columnNumber, int lineNumber, int expectedAmount, int actualAmount) {
        super(columnNumber, lineNumber);
        this.expectedAmount = expectedAmount;
        this.actualAmount = actualAmount;
    }

    public String toString() {
        return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Expected " + expectedAmount + " parameters but got " + actualAmount + " parameters";
    }
}
