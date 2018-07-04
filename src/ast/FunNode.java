package ast;

import lib.FOOLlib;

import java.util.ArrayList;

public class FunNode  extends FunAbstractNode {
    /**
     * Constructor for FunctionNode.
     *
     * @param id --> Function name
     * @param type --> Function type
     * @param listVar --> Function parameters
     * @param progNode --> Function body program
     * @param retNode --> Function return node
     */
    public FunNode (String id, Node type, ArrayList<Node> listVar, Node progNode, Node retNode) {
        super(id, type, listVar, progNode, retNode);
    }

    @Override
    /**
     *
     * @return
     */
    public String codeGeneration() {
        String popDec="";
        if (progNode instanceof LetInExpNode){
            for (int i=0; i<((LetInExpNode)progNode).getListDecSize();i++){
                popDec+="pop\n";
            }
        }
        String popVar="";
        for (Node dec:listVar){
            popVar+="pop\n";
        }
        String funl=FOOLlib.freshFunLabel();
        String end="";
        if(id.equals("main")){
            FOOLlib.putLabelMain(funl);
            end="b "+FOOLlib.getLabelEnd()+"\n";
        }else {
            end= "sfp\n"+  // setto $fp a valore del CL
                    "lrv\n"+ // risultato della funzione sullo stack
                    "lra\n"+"js\n"; // salta a $ra
        }
        String retCod="";
        if(retNode!=null){
            retCod=retNode.codeGeneration();
        }else{
            retCod="push 0\n";
        }
        String progCod="";
        if (progNode!=null){
            progCod=progNode.codeGeneration();
        }
        FOOLlib.putCode(funl+":\n"+
                "cfp\n"+ //setta $fp a $sp
                "lra\n"+ //inserimento return address
                progCod+
                retCod+
                "srv\n"+ //pop del return value
                popDec+
                "sra\n"+ // pop del return address
                "pop\n"+ // pop di AL
                popVar+
                end
        );

        return "push "+ funl +"\n";
    }

}