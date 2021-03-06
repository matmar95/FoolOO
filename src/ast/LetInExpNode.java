package ast;

import util.Environment;
import util.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class LetInExpNode implements Node {

    private ArrayList<Node> listDec;
    private Node stms;
    private int offset=-2;

    /**
     * Constructor for LetInExpNode.
     *
     * @param listDec --> Arraylist of declarations inside Let In
     * @param stms --> Node expression
     */
    public LetInExpNode (ArrayList<Node> listDec, Node stms) {
        this.listDec=listDec;
        this.stms=stms;
    }

    public int getListDecSize() {
        return listDec.size();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Prints structure of LetInExpNode.
     *
     * @param s parent Indentation, incremented at every toPrint
     * @return updated string that prints Abstract Syntax Tree Structure
     */
    public String toPrint(String s) {
        String returnString = s + "LetInExpNode" + "\n";

        returnString += s + "Let " + "\n";
        for(Node ntp : listDec){
            //richiama il toPrint per ciascun nodo dichiarazione
            returnString += s + ntp.toPrint(s + "   ") + "\n";
        }
        returnString += s + "In " + "\n";

        if (stms!=null) {
            returnString += s + this.stms.toPrint(s + "   ") + "\n";
        }

        return returnString;
    }

    /**
     * Check LetInExpNode's semantic and call checkSemantic method on every childNode.
     *
     * Child node: listDec (list declaration)
     *
     * @param env -> Environment that holds previously parsed information
     * @return  updated ArrayList of semantic errors
     */
    public ArrayList<SemanticError> checkSemantics(Environment env) {

        ArrayList<SemanticError> semanticErrors = new ArrayList<SemanticError>();

        //Si ottiene l'hashmap dello scope corrente
        HashMap<String,STentry> hashMap = env.getHashMapNL(env.getNestingLevel());

        //nome della chiave del nodo
        String idPutHM;

        env.setOffset(offset);
        for(Node node : listDec){
            //controllo nodo funzione
            if(node instanceof FunNode ){
                idPutHM = "fun#";

                //casting del nodo in FunNode
                FunNode funNode=(FunNode) node;

                //creazione chiave del nodo funzione
                idPutHM += funNode.getId() +"%";
                idPutHM += ((TypeNode)funNode.getType()).getType();

                //arraylist di parametri della funzione
                ArrayList<Node> parList = funNode.getListVar();
                for (Node nodePar : parList) {
                    //ottengo il type di ciascun parametro
                    TypeNode typeVar = (TypeNode) ((VarDecNode) nodePar).getType();

                    //completata creazione stringa chiave
                    idPutHM += "%" + typeVar.getType();
                }
                /*
                Inseriamo nell'ambiente l'identificatore della funzione con un'entry fasulla,
                cambiata in seguito con l'entry corretta.
                 */
                if ( hashMap.put(idPutHM,new STentry(env.getNestingLevel(),env.getOffsetDec())) != null ){
                    //funzione già dichiarata nell'ambiente corrente
                    semanticErrors.add(new SemanticError("Function "+idPutHM+" already declared"));
                }
            }
        }

        //richiamo checkSemantic per ciascun nodo di listDec all'interno di let in
        for(Node node : listDec){
            semanticErrors.addAll(node.checkSemantics(env));
        }

        //richiamo checkSemantic nella exp se esiste
        if (stms!=null) {
            semanticErrors.addAll(stms.checkSemantics(env));
        }

        //ritorno l'arraylist di errori semantici
        return semanticErrors;
    }

    /**
     *  It call typeCheck for every declaration child and for exp child.
     *
     * @return instance of VoidTypeNode()
     */
    public Node typeCheck() {
        for (Node node : listDec) {
            //richiamo il type check per ciascuna dichiarazione all'interno di let in
            node.typeCheck();
        }

        if (stms!=null){
            //richiamo type check per exp se esiste
            stms.typeCheck();
        }

        return new VoidTypeNode();
    }

    /**
     *
     * @return string of generated code
     */
    public String codeGeneration() {
        String code="";
        for (Node node : listDec) {
            if(node instanceof FunNode ){
                //richiamo il codeGeneration per ciascuna dichiarazione di funzione all'interno di let
                code+=node.codeGeneration();
            }
        }
        for (Node node : listDec) {
            if(!(node instanceof FunNode)){
                //richiamo il codeGeneration per ciascuna dichiarazione di variabile all'interno di let
                code+=node.codeGeneration();
            }
        }
        if (stms!=null){
            code+=stms.codeGeneration();
        }
        return code;
    }
}