grammar SVM;

@header {
import java.util.HashMap;
}

@lexer::members {
public int lexicalErrors=0;
}

@parser::members {
      
    public int[] code = new int[ExecuteVM.CODESIZE];    
    private int i = 0;
    private HashMap<String,Integer> labelAdd = new HashMap<String,Integer>();
    private HashMap<Integer,String> labelRef = new HashMap<Integer,String>();
        
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
assembly:
    (
        PUSH n=NUMBER           {code[i++] = PUSH;
			                    code[i++] = Integer.parseInt($n.text);}

	    | PUSH l=LABEL          {code[i++] = PUSH;
                                labelRef.put(i++,$l.text);}

	    | POP		            {code[i++] = POP;}

	    | ADD		            {code[i++] = ADD;}

	    | SUB		            {code[i++] = SUB;}

	    | MULT	                {code[i++] = MULT;}

	    | DIV		            {code[i++] = DIV;}

	    | STOREW	            {code[i++] = STOREW;}

	    | LOADW                 {code[i++] = LOADW;}

	    | l=LABEL COL           {labelAdd.put($l.text,i);}

	    | BRANCH l=LABEL        {code[i++] = BRANCH;
                                labelRef.put(i++,$l.text);}

        | BRANCHEQ l=LABEL      {code[i++] = BRANCHEQ;
                                labelRef.put(i++,$l.text);}

	    | BRANCHLESSEQ l=LABEL  {code[i++] = BRANCHLESSEQ;
                                labelRef.put(i++,$l.text);}

        | BRANCHGREATEREQ l=LABEL  {code[i++] = BRANCHGREATEREQ;
                                        labelRef.put(i++,$l.text);}

	    | JS                    {code[i++] = JS;}

	    | LOADRA                {code[i++] = LOADRA;}

	    | STORERA               {code[i++] = STORERA;}

	    | LOADRV                {code[i++] = LOADRV;}

	    | STORERV               {code[i++] = STORERV;}

	    | LOADFP                {code[i++] = LOADFP;}

	    | STOREFP               {code[i++] = STOREFP;}

	    | COPYFP                {code[i++] = COPYFP;}

	    | COPYFPM                {code[i++] = COPYFPM;}

	    | LOADHP                {code[i++] = LOADHP;}

	    | STOREHP               {code[i++] = STOREHP;}

	    | PRINT                 {code[i++] = PRINT;}

	    | HALT                  {code[i++] = HALT;}

	    | PUSHTOHP              {code[i++] = PUSHTOHP;}

        | LOADFPO               {code[i++] = LOADFPO;}

        | STOREFPO              {code[i++] = STOREFPO;}

        | COPYFPO                {code[i++] = COPYFPO;}

        | COPYFPP                {code[i++] = COPYFPP;}

        | PUSHMINUS             {code[i++] = PUSHMINUS;}

        | LOADFP1               {code[i++] = LOADFP1;}

        | STOREPN              {code[i++] = STOREPN;}

        | LOADPN               {code[i++] = LOADPN;}

        | COPYFPOF             {code[i++] = COPYFPOF;}

        | LOADFPOF              {code[i++] = LOADFPOF;}

    )*  {
            for (Integer refAdd: labelRef.keySet() ) {
                      code[refAdd]=labelAdd.get(labelRef.get(refAdd));
            }
        };
 	 
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
 
PUSH  	        : 'push' ; 	// pushes constant in the stack
POP	            : 'pop' ; 	// pops from stack
ADD	            : 'add' ;  	// add two values from the stack
SUB	            : 'sub' ;	// add two values from the stack
MULT	        : 'mult' ;  // add two values from the stack
DIV	            : 'div' ;	// add two values from the stack
STOREW	        : 'sw' ; 	// store in the memory cell pointed by top the value next
LOADW	        : 'lw' ;	// load a value from the memory cell pointed by top
BRANCH	        : 'b' ;	    // jump to label
BRANCHEQ        : 'beq' ;	// jump to label if top == next
BRANCHLESSEQ    : 'bleq' ;  // jump to label if top <= next
BRANCHGREATEREQ : 'bgeq';   // jump to label if top >= next
JS	            : 'js' ;	// jump to instruction pointed by top of stack and store next instruction in ra
LOADRA	        : 'lra' ;	// load from ra
STORERA         : 'sra' ;	// store top into ra
LOADRV	        : 'lrv' ;	// load from rv
STORERV         : 'srv' ;	// store top into rv
LOADFP	        : 'lfp' ;	// load frame pointer in the stack
STOREFP	        : 'sfp' ;	// store top into frame pointer
COPYFP          : 'cfp' ;   // copy stack pointer into frame pointer
COPYFPM         : 'cfpm' ;   // copy stack pointer-1 into frame pointer
LOADHP	        : 'lhp' ;	// load heap pointer in the stack
STOREHP	        : 'shp' ;	// store top into heap pointer
PRINT	        : 'print' ;	// print top of stack
PUSHTOHP        : 'pthp' ;  // pushes top of stack to heap
STOREFPO        : 'sfpo' ;
LOADFPO         : 'lfpo' ;
COPYFPO         : 'cfpo' ;   // copy stack pointer into frame pointer
COPYFPP         : 'cfpp' ;
PUSHMINUS       : 'pushminus' ;
LOADFP1         : 'lfp1' ;
LOADPN          : 'lpn' ;
STOREPN         : 'spn' ;
COPYFPOF        : 'cfpof' ;
LOADFPOF        : 'lfpof' ;
HALT	        : 'halt' ;	// stop execution

COL	        : ':' ;
LABEL	    : ('a'..'z'|'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')* ;
NUMBER	    : '0' | ('-')?(('1'..'9')('0'..'9')*) ;

WHITESP     : ( '\t' | ' ' | '\r' | '\n' )+   -> channel(HIDDEN);

ERR   	    : . { System.err.println("Invalid char: "+ getText()); lexicalErrors++;  } -> channel(HIDDEN);

