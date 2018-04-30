package CompilerError;

public class AssignmentError extends Error{
	private String idType;
    private String exprType;

    public AssignmentError(int columnNumber, int lineNumber, String idType, String exprType ) {
        super(columnNumber, lineNumber);
        this.idType = idType;
        this.exprType = exprType;
    }
    @Override
    public String toString() {
        return "line " + lineNumber + ":" + columnNumber + " Tried to assign type " + exprType + " to an identifier of type " + idType;
    }
}