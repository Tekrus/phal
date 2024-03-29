import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CompilerError.*;
import Warnings.*;
import enums.Type;

import java.util.Stack;

public class SymbolTable {

	private HashMap<String, FuncNode> functionMap = new HashMap<>();
	private Stack<HashMap<String, AstNode>> symbolTable = new Stack<>();
	private List<Type> componentIncludesList = new ArrayList<>();

	public List<Type> getCompInclMap() {
		return componentIncludesList;
	}
	
	/*ST methods*/
	public void openScope() {
		this.symbolTable.push(new HashMap<String, AstNode>());
	}
	public void closeScope() {
		this.symbolTable.pop();
	}
	public void addDeclarationToSymbolTable(DclNode node) {
		if(symbolTable.peek().containsKey(node.idNode.id))
		{
			MainClass.CompileErrors.add(new RedeclarationError(
					node.columnNumber, node.lineNumber, node.idNode.id));
		}
		else
		{
			symbolTable.peek().put(node.idNode.id, node);
			if(node instanceof CmpDclNode) {
				if(!componentIncludesList.contains(((CmpDclNode) node).advTypeNode.Type)) {
					componentIncludesList.add(((CmpDclNode) node).advTypeNode.Type);
				}
			}
		}


	}

	public void addAssignmentToSymbolTable(AssignmentNode node) {
		HashMap<String, AstNode> map =  symbolTable.peek();
		String key = node.idNode.id;
		
		if(map.containsKey(key))
		{
			DclNode dcl = (DclNode)map.get(key);
			if(!dcl.isUsed) {
				dcl.isUsed = true;
				map.put(key, dcl);
			}
			node.idNode.dclNode = dcl;
			
		}
		else
		{
			MainClass.CompileErrors.add(new NotDeclaredError(
					node.columnNumber, node.lineNumber, node.idNode.id));
		}
	}
	public void addParamToSymbolTable(ParamNode node) {
		if(symbolTable.peek().containsKey(node.idNode.id))
		{
			MainClass.CompileErrors.add(new RedeclarationError(
					node.columnNumber, node.lineNumber, node.idNode.id));

		}
		else
		{
			symbolTable.peek().put(node.idNode.id, node);
		}

	}
	
	public void addIdToSymbolTable(IdNode node) {
		HashMap<String, AstNode> map =  symbolTable.peek();
		String key = node.id;
		
		if(map.containsKey(key))
		{
			DclNode dcl = (DclNode)map.get(key);
			if(!dcl.isUsed) {
				dcl.isUsed = true;
				map.put(key, dcl);
			}
			node.dclNode = dcl; 
		}
		else
		{
			MainClass.CompileErrors.add(new NotDeclaredError(
					node.columnNumber, node.lineNumber, node.id));
		}
	}
	
	
	
	/*
	 * Checks top of stack (current scope)
	 * 
	 * */
	public void checkVariablesAreUsed() {
		for(AstNode node : symbolTable.peek().values()) {
			if(node instanceof DclNode) {
				if(!((DclNode) node).isUsed) {
					MainClass.CompileWarnings.add(
							new VarNotUsedWarning(node.columnNumber, node.lineNumber, ((DclNode) node).idNode.id));
				}
			}
		}
		
	}
	
	public void checkFunctionsAreUsed() {
		for(FuncNode node: functionMap.values()) {
			if(!node.isUsed) {
				MainClass.CompileWarnings.add(new FuncNotUsedWarning(node.columnNumber, node.lineNumber, node.idNode.id));
			}
		}
	}
	
	/*FuncMap methods*/
	public void addToFuncMap(FuncNode node) {
		if(functionMap.containsKey(node.idNode.id))
		{
			MainClass.CompileErrors.add(new RedeclarationError(
					node.columnNumber, node.lineNumber, node.idNode.id));
		}
		else
		{
			functionMap.put(node.idNode.id, node);
		}

	}

	public FuncNode getFunctionFromFuncMap(FuncCallNode node) {
		FuncNode func = functionMap.get(node.idNode.id);
		if(func == null) {
			MainClass.CompileErrors.add(
					new FunctionNotDeclaredError( node.columnNumber,node.lineNumber, node.idNode.id)
			);
		}
		else{
			func.isUsed = true;
		}
		return func;
	}
	
	
}
