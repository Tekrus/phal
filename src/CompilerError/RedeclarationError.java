package CompilerError;

public class RedeclarationError extends Error {
	private String referenceName;
	
	public RedeclarationError (int columnNumber, int lineNumber, String referenceName) {
        super(columnNumber, lineNumber);
        this.referenceName = referenceName;
    }
	
	 @Override
	 public String toString() {
	     return lineNumber + ": " + referenceName + " already declared in this scope";
	 }
}