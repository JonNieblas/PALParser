/*
 * Opcode contains all methods dealing with opcodes, as well as the construction of the opList arrayList
 */

import java.util.ArrayList;

public class Opcode {

    public Opcode (){//empty constructor for now
    }

    public void OpcodeHandler(String opcode){
        if(opcode.equals("ADD") || opcode.equals("SUB")){
            //direct it to correct method
        }
        else if(opcode.equals("MUL") || opcode.equals("DIV")){
            //direct it to correct method
        }
        else if(opcode.equals("MOVE") || opcode.equals("COPY")){
            //direct it to correct method
        }
        else if(opcode.equals("INC") || opcode.equals("DEC")){
            //direct it to correct method
        }
        else if(opcode.equals("BEQ") || opcode.equals("BGT")){

        }
        else if(opcode.equals("BR")){

        }
    }
}
