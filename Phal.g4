/**
 * Define a grammar called Phal
 * Beware might need NEWLINE+* before include if NEWLINE+ or whitespace becomes a problem
 */

grammar Phal;

program
	:	NEWLINE* (include)* setup NEWLINE* repeat NEWLINE* (func NEWLINE*)* NEWLINE* EOF
	;
  
include
	:	'using' ID NEWLINE+
	;

setup        
	: 	'setup' NEWLINE* '{' NEWLINE* (setupCnt)* NEWLINE* '}'
	;

setupCnt    
	: 	dcl NEWLINE+
	| 	stmt 
	;

dcl         
	: 	varDcl    
	|   cmpDcl 
	|   advDataType 
	;

varDcl        
	: 	type ID 
	| 	type ID ':=' expr 
	|	type ID ':=' expr (',' expr)*
	;
  
type        
	: 	'number' 
	|  	'text'  
	|  	'bool' 
	|   advType
	;
  
advDataType    
	: 	group   
	|   list 
	;

cmpDcl    	
	: 	advType ID ':=' 'pin' NUMBER ( ',' NUMBER)*
	;

advType    
	: 	'lightbulb'  
	|  	'coffeeMachine'   
	|  	'temperatureSensor' 
	;

group        
	: 	'group' ID NEWLINE* '{' NEWLINE* (ID NEWLINE+)+ '}' 
	;

list        
	: 	'list' type ID '{' listCnt ( ',' listCnt)* '}' 
	;

listCnt        
	: 	ID 
	| 	VALUE 
	;

stmt        
	: 	selective  NEWLINE+
	|   iterative  NEWLINE+
	|   funcCall	
	|   assignment	
	|	returnStmt 
	;

selective    
	: 	ifStmt    
	|   switchStmt
	;

switchStmt        
	:  	'switch' '(' expr ')' '{' caseList '}'   
	;

 
caseList    
	: 	(caseStmt NEWLINE+)+ defaultCase 
	;

caseStmt        
	: 	'case' VALUE ':' (stmt)* NEWLINE+
	;

defaultCase    
	: 	'default' ':' (stmt)* NEWLINE+
	;

ifStmt        
	: 	'if' '(' expr ')' 'then' NEWLINE* '{' NEWLINE* (stmt)* NEWLINE* '}'
	|   'if' '(' expr ')' 'then' NEWLINE* '{' NEWLINE* (stmt)* NEWLINE*  '}' 'else'  '{'  NEWLINE* (stmt)* NEWLINE*  '}'
	|   'if' '(' expr ')' 'then' NEWLINE* '{' NEWLINE* (stmt)* NEWLINE*  '}' 'else'  ifStmt  
	;

iterative    
	: 	loop 
	;

loop        
	: 	'loop' NUMBER 'times' '{' NEWLINE*  (stmt)*  NEWLINE* '}'    
	|   'loop' 'until' expr '{' NEWLINE* (stmt)* NEWLINE*  '}' 
	;

funcCall    
	:  	'call' ID 'with' '(' none ')'
	| 	'call' ID 'with' '(' callCnt ')'
	;

callCnt        
	: 	expr ( ',' expr)* 
	;

assignment    
	: 	ID ':=' expr NEWLINE+
	| 	ID '+=' expr NEWLINE+
	| 	ID '-=' expr NEWLINE+
	|	ID '.' ID ':=' expr (',' expr)* NEWLINE+
	|	ID '.' ID '+=' expr (',' expr)* NEWLINE+
	|	ID '.' ID '-=' expr (',' expr)* NEWLINE+
	;

repeat    
	: 	'repeat' NEWLINE* '{' NEWLINE* repeatCnt NEWLINE* '}'  
	;

repeatCnt
	: 	stmt*
	;

func        
	: 	'define' ID 'with' '(' parameters? ')' 'returnType' rType NEWLINE* '{' NEWLINE* funcCnt* NEWLINE* returnStmt? '}' 
	;

funcCnt		
	:	varDcl NEWLINE* 
	| 	stmt NEWLINE*
	;

rType		
	: 	type 
	| 	'none'
	;

parameters    
	:  	param ( ',' param)*  
	|   none
	;

param        
	: 	type ID
	;

returnStmt    
	: 'return' (ID | VALUE | 'none') NEWLINE*
	;
	
expr
  :		BOOL																			# litBoolExpr
  |		NUMBER																			# litNumExpr
  |		ID																				# idRefExpr
  |		TEXT																			# litTextExpr
  |		ID '.' ID																		# idRefExpr
  |		funcCall																		# funcExpr
  |		'(' expr ')'																	# parenExpr
  |   	('!'|'not') expr																# unaryExpr
  |		expr ('+'|'-') expr																# infixExpr
  |		expr ('*'|'/'|'%') expr															# infixExpr
  |		expr ('!='|'='| 'is' | 'is not') expr            								# infixExpr	
  |		expr ('<'|'>'|'less than'|'greater than') expr									# infixExpr
  |		expr ('<='|'>='|'less than or equal to'|'greater than or equal to') expr		# infixExpr
  |		expr ('and'|'&') expr															# infixExpr
  |		expr ('or'|'|') expr															# infixExpr 
  ;
 

none : 'none';

TEXT 			: '"' ~('\r' | '\n' | '"')* '"' ;
ID 				: LETTER (LETTER | DIGIT)*;
fragment INTEGER 		: DIGIT+;
fragment FLOAT 			: (DIGIT | [1-9](DIGIT)+)'.'(DIGIT | (DIGIT)*[1-9]);
NUMBER 			: ('-')? (INTEGER | FLOAT) ;
BOOL 			: ('true'|'false' | 'on' |'off'); 

COMMENT 		: '#' ~('\r' | '\n')* 	-> skip ;
MULTILINECOMMET	: '/*' .*? '*/' 		-> skip ;
WS  			:   [ \t]+ 				-> channel(HIDDEN) ;
NEWLINE			:  [\r\n];
VALUE 			: NUMBER | BOOL | TEXT ;  

fragment LETTER			: [a-zA-Z];
fragment DIGIT 			: '0'..'9';
