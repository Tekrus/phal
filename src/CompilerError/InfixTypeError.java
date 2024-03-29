package CompilerError;
import enums.*;

public class InfixTypeError extends Error {
	private String typeOne;
	private String typeTwo;
	private InfixOperator operator;

	public InfixTypeError(int columnNumber, int lineNumber, String typeOne, String typeTwo, InfixOperator operator) {
		super(columnNumber, lineNumber);
		this.typeOne = typeOne;
		this.typeTwo = typeTwo;
		this.operator = operator;
		
	}
	@Override
	public String toString() {
		
		switch(operator) {
			case PLUS: 
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Could not use the '" + operator + "' operator with expressions of the types '"
					+ typeOne + "' and '" + typeTwo  + "'. Both must be of type NUMBER or TEXT";
				
			case MINUS:
			case MULTIPLY:
			case DIVISION:
			case MODULO:
			case LESSTHANEQUAL:
			case GREATERTHANEQUAL:
			case GREATERTHAN:
			case LESSTHAN:
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Could not use the '" + operator + "' operator with expressions of the types '"
				+ typeOne + "' and '" + typeTwo + "'. Both must be of type NUMBER";
		
			case AND:
			case OR:
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Could not use the '" + operator + "' operator with expressions of the types '"
				+ typeOne + "' and '" + typeTwo + "'. Both must be of type BOOL";
			case EQUAL:
			case NOTEQUAL:
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Could not use the '" + operator + "' operator with expressions of types '"
				+ typeOne + "' and '" + typeTwo + "'. Both must be of type NUMBER, TEXT or BOOL";
				
			default:
				return "ERROR - Line: " + lineNumber + ":" + columnNumber + " - Undefined type error";
		}
		
	}
}
