package CompilerError;

public class ReturnTypeError extends Error {
	private String actualType;
    private String expectedType;
    private String functionName;
    private boolean isListError;
    private boolean isFuncReturnList;
    
	public ReturnTypeError(int columnNumber, int lineNumber,String functionName, String actualType , String expectedType, boolean isListError, boolean isFuncReturnList) {
		super(columnNumber, lineNumber);
		this.functionName = functionName;
		this.actualType = actualType;
        this.expectedType = expectedType;	
        this.isListError = isListError;
        this.isFuncReturnList = isFuncReturnList;
	}
	
	@Override
	public String toString() {
		if(isListError) {
			if(isFuncReturnList) {
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - The function '" + functionName + "' expects a return of type 'list of" + expectedType + "' but the return is of type '" + actualType + "'";
			}
			else {
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - The function '" + functionName + "' expects a return of type '" + expectedType + "' but the return is of type 'list of" + actualType + "'";
			}
		}
	    return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - The function '" + functionName + "' expects a return of type '" + expectedType + "' but the return is of type '" + actualType + "'";
	}
}
