package ast;

import lib.FOOLlib;
import parser.ExecuteVM;
import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class AsmNode implements Node {

    private String id;  //Variable's ID
    private Node value; //Value assigned to id
    private STentry entry; //field that holds AsmNode SymbolTable entry
    private int nestingLevel;

    /**
     * Constructor for AsmNode.
     *
     * @param id -> Variable's ID
     * @param value -> Value assigned to id
     */
    public AsmNode(String id, Node value) {
        this.id=id;
        this.value=value;
        this.entry=null;
    }

    /**
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Prints structure of AsmNode.
     *
     * @param s parent Indentation, incremented at every toPrint
     * @return updated string that prints Abstract Syntax Tree Structure
     */
    public String toPrint(String s) {
        String returnString=s + "AsmNode\n" + s +s + "Id:" + id+ "\n";
        returnString+=value.toPrint(s+"   ");
        if(entry!=null){
            returnString+=entry.toPrint(s+"   ");
        }
        return returnString;
    }

    /**
     * Checks AsmNode's semantic and call checkSemantic method on every child Node
     *
     * @param env -> Environment that holds previously parsed information
     * @return updated ArrayList of semantic errors
     */
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();

        /* NestingLevel attuale*/
        int j=env.getNestingLevel();

        /* Dichiarazione di STentry in cui salvare
        la dichiarazione della variabile (se trovata) */
        STentry tmpEntry=null;

        /* Ciclo che scorre tutti i NestingLevel superiori
        per cercare una dichiarazione delle variabile */
        while (j>=0 && tmpEntry==null){
            /* Effettuo una ricerca nell'HashMap relativa al j-esimo nestingLevel
            della chiave "id". Se la trova salva l'entry relativa ed esce dal ciclo. */
            tmpEntry = env.getHashMapNL(j--).get(id);
            if(tmpEntry!=null){
                if(value instanceof NewClassNode){
                    TypeNode type=new IdTypeNode(((NewClassNode) value).getId());
                    ((IdTypeNode) type).setExtClassId(((NewClassNode) value).getExtClassId());
                    tmpEntry.addType(type);
                    env.getHashMapNL(j).replace(id,tmpEntry);
                    ((NewClassNode) value).setIdCallMethod(id);
                }else if (value instanceof IfNode){
                    Node thenB=((IfNode)value).getThenBranch();
                    Node elseB=((IfNode)value).getElseBranch();
                    if(thenB instanceof NewClassNode){
                        ((NewClassNode) thenB).setIdCallMethod(id);
                    }
                    if(elseB instanceof NewClassNode){
                        ((NewClassNode) elseB).setIdCallMethod(id);
                    }

                }
            }
        }
        /* Controlla se trova l'entry della dichiarazione */
        if(tmpEntry==null){
            /* Se non la trova
            Aggiungo un errore semantico nella Lista degli errori per "Variabile non dichiarata */
            semanticErrors.add(new SemanticError("Id " + id + " is not declared"));
        } else {
            /* Salvo l'entry della variabile dichiarate nei nestingLevel precedenti nel nodo
            (per avere l'informazione del tipo) */
            entry=tmpEntry;
            nestingLevel=env.getNestingLevel();
        }
        /* richiama il checkSemantic sul nodo value e
        aggiunge tutti gli eventuali errori semantici all'array degli errori */
        semanticErrors.addAll(value.checkSemantics(env));

        return semanticErrors;
    }

    /**
     * Check that value field is subtype of id's type (saved in node's entry field)
     *
     * @return instance of VoidTypeNode()
     */
    public Node typeCheck() {
        if( !FOOLlib.isSubtype(value.typeCheck(), entry.getType()) ){
            /* Stampa un errore di tipo e blocca l'esecuzione */
            System.out.println("TypeCheck error for assignement to " + id );
            System.exit(0);
        }
        return new VoidTypeNode();
    }

    /**
     * Generated code for the AsmNode.
     * @return updated codeGeneration string
     */
    public String codeGeneration() {

        String returnString =value.codeGeneration()+//Carico in cima allo stack il valore da assegnare
                        "push "+entry.getOffset()+"\n"; //metto offset sullo stack


        if(entry.getOffsetObj()==-1){
            returnString += "lfpof\n";
        }else{
            returnString +="lfp\n"; //carico il fp sullo stack
            //risalgo la catena statica
            for (int i=0; i<nestingLevel-entry.getNestinglevel(); i++){
                returnString+="lpn\nadd\nlw\n";   //Faccio un loadword per ogni scope da risalire
                //Ottengo l'indirizzo dello scope a cui ho la variabile
            }
        }


        returnString += "add\n"+ //Carico in cima allo stack il valore dell'inidirizzo in cui è salvata la variabile nello stack
                "sw\n"; //Sovrascrivo sullo stack il valore da assegnare all'indirizzo ottenuto


        if(value instanceof NewClassNode){
            NewClassNode newClassNode=(NewClassNode) value;
            if(newClassNode.isCodGenMethod()){
                returnString+=newClassNode.getMethodCall().codeGeneration()+"pop\n";
            }
        }

        return returnString;
    }
}