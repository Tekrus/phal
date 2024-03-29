package CompilerError;

public class ParameterMismatchError extends Error{
	private String expectedSignature;
    private String actualSignature;
    private int paramNum;
    private boolean isListError;
    private boolean formalParamIsList;

    public ParameterMismatchError(int columnNumber, int lineNumber, int paramNum, String expectedSignature, String actualSignature, boolean isListError, boolean formalParamIsList) {
        super(columnNumber, lineNumber);
        this.paramNum = paramNum;
        this.expectedSignature = expectedSignature;
        this.actualSignature = actualSignature;
        this.isListError = isListError;
        this.formalParamIsList = formalParamIsList;
    }

    @Override
    public String toString() {
    	if(isListError) {
    		if(formalParamIsList) {
    			return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Parameter " + paramNum +  ": Expected parameter of type 'list of " + expectedSignature + "' but got '" + actualSignature + "'";
    		}
    		else {
    			return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Parameter " + paramNum +  ": Expected parameter of type '" + expectedSignature + "' but got 'list of " + actualSignature + "'";
    		}
    	}
    	else {
    		return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Parameter " + paramNum +  ": Expected parameter of type '" + expectedSignature + "' but got '" + actualSignature + "'";
    	}
    }
}
