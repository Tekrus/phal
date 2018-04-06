import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainClass {

	public static void main(String args[]) 
	{
		String text = "using asd \n setup{number a3  \n number t := 4 \n } repeat{}";
		
		try {
			ANTLRInputStream inputStream = new ANTLRInputStream(text);
			PhalLexer lexer = new PhalLexer(inputStream);
			CommonTokenStream ts = new CommonTokenStream(lexer);
			PhalParser parser = new PhalParser(ts);
			PhalParser.ProgramContext cst = parser.program();
			
			System.out.println(cst.toStringTree());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}
}
