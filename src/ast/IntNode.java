package ast;

import util.Environment;
import util.SemanticError;

import java.util.ArrayList;

public class IntNode implements Node {

    private Node exp;

    private Integer val;

    public IntNode(Integer val) {
        this.val=val;
    }

    public String toPrint(String s) {

        return "Start\n" + val.toString() ;
    }

    public ArrayList<SemanticError> checkSemantics(Environment env) {

        return exp.checkSemantics(env);
    }

    public Node typeCheck() {
        return exp.typeCheck();
    }

    public String codeGeneration() {
        return exp.codeGeneration()+"halt\n";
    }

}