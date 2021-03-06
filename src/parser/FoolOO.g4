grammar FoolOO;

@lexer::members {
   public int lexicalErrors=0;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
start   : (decclass)* main
        ;

decclass   : CLASS ID ( EXTENDS ID )? LPAR ( vardec ( COMMA vardec)* )? RPAR CLPAR (funClass | funconstructor)+ CRPAR
            ;

main   : type MAIN LPAR RPAR CLPAR prog CRPAR
        ;

prog    : (stms)?       #singleExp
        | let (stms)?   #letInExp
        ;

let     : LET (dec)+ IN
        ;

progFun : (stms)?       #singleExpFun
        | letFun (stms)?   #letInExpFun
        ;

letFun  : LET (varasm)+ IN
        ;

dec     : varasm           #varAssignment
        | fun              #funDeclaration
        ;

vardec  : type ID
        ;

varasm  : vardec ASM exp SEMIC      #expDecAsignment
        |  vardec ASM stm          #stmDecAsignment
        ;

funconstructor  : ID LPAR ( vardec ( COMMA vardec)* )? RPAR CLPAR progFun CRPAR
                ;

fun     : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR CLPAR progFun (ret)? CRPAR
        ;

funClass: type ID LPAR ( vardec ( COMMA vardec)* )? RPAR CLPAR progFun (ret)? CRPAR
                ;

ret     : RETURN exp SEMIC                                      #returnFunExp
        | RETURN stm                                           #returnFunStms
        ;

type    : INT
        | BOOL
        | VOID
        | ID
        ;

exp     : (MINUS)? left=term ((PLUS | MINUS) right=exp)?
        ;

term    : left=factor ((TIMES | DIV) right=term)?
        ;

factor  : (NOT)? left=value ((EQ|GTEQ|LTEQ|AND|OR) factorRight)?
        ;

factorRight: (NOT)? right=value;

stm     : IF cond=exp THEN CLPAR thenBranch=stms CRPAR (ELSE CLPAR elseBranch=stms CRPAR)?       #stmIf
        | PRINT LPAR exp RPAR SEMIC                                       #stmPrint
        | funExp SEMIC                                          #callFunExp
        | ID DOT funExp SEMIC                                   #callMethod
        | ID ASM stm                                            #stmAssignment
        | ID ASM exp SEMIC                                      #stmValAssignment
        ;

funExp: ID LPAR (exp (COMMA exp)* )? RPAR  ;

stms    : (stm)+
        ;

value   : INTEGER                               #intVal
        | ( TRUE | FALSE )                      #boolVal
        | LPAR exp RPAR                         #baseExp
        | IF cond=exp THEN CLPAR thenBranch=exp CRPAR (ELSE CLPAR elseBranch=exp CRPAR)?       #ifExp
        | stm                                  #stmsExp
        | ID                                    #varExp
        | NULL                                  #nullVal
        | NEW ID LPAR (exp (COMMA exp)* )? RPAR #newClass
        | funExp                                #funExpValue
        | ID DOT funExp                         #callMethodValue
        ;


/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
DOT     : '.';
SEMIC   : ';' ;
COLON   : ':' ;
COMMA   : ',' ;
EQ      : '==' ;
ASM     : '=' ;
PLUS    : '+' ;
MINUS   : '-' ;
TIMES   : '*' ;
DIV     : '/' ;
AND     : '&&' ;
OR      : '||' ;
GTEQ    : '>=' ;
LTEQ    : '<=' ;
NOT     : '!' ;
TRUE    : 'true' ;
FALSE   : 'false' ;
LPAR    : '(' ;
RPAR    : ')' ;
CLPAR   : '{' ;
CRPAR   : '}' ;
QLPAR   : '[' ;
QRPAR   : ']' ;
IF      : 'if' ;
THEN    : 'then' ;
ELSE    : 'else' ;
PRINT   : 'print' ;
LET     : 'let' ;
IN      : 'in' ;
INT     : 'int' ;
BOOL    : 'bool' ;
VOID    : 'void' ;
RETURN  : 'return' ;
CLASS   : 'class';
EXTENDS : 'extends';
NEW     : 'new';
NULL    : 'null';
MAIN    : 'main';

//Numbers
fragment DIGIT : '0'..'9';
INTEGER       : DIGIT+;

//IDs
fragment CHAR  : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPED SEQUENCES
WS              : (' '|'\t'|'\n'|'\r') -> skip;
LINECOMENTS    : '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip;

 //VERY SIMPLISTIC ERROR CHECK FOR THE LEXING PROCESS, THE OUTPUT GOES DIRECTLY TO THE TERMINAL
 //THIS IS WRONG!!!!
ERR     : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN);