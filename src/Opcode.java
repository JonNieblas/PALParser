/*
 * Opcode contains all methods dealing with opcodes, as well as the construction of the opList arrayList
 */

import java.util.ArrayList;
import java.util.List;

public class Opcode {

    private ArrayList<String> validSources = new ArrayList<String>();
    private ArrayList<String> validDestination = new ArrayList<String>();

    /*
     * creates arrays when new Opcode obj is created
     */
    public Opcode (){
        RegisterListCreator();
    }

    /*
     * OpcodeHandler will take the opcode and pass the line it was contained in to
     * the correct method, along with the linesToLog list.
     */
    public void OpcodeHandler(String opcode, String line, List<String> linesToLog){
        if(opcode.equals("ADD") || opcode.equals("SUB") || opcode.equals("MUL") || opcode.equals("DIV")){
            ASMDOpcode(line, linesToLog);
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

    /*
     * Observes an ASMD line to make sure there are no errors
     */
    public void ASMDOpcode(String line, List<String> linesToLog){
        String[] wordSplitter = line.split(" ");
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);

        for (String word : wordSplitter){
            if((!validSources.contains(word) && wordCount > 0 && wordCount < 3) || (!validDestination.contains(word) && wordCount == 3)){
                err.AddToErrorList(1);//error for ill-formed operand
            }
            wordCount++;
        }
        linesToLog.add(line);
        err.ErrorsToLog();//writes errors to .log file
    }

    /*
     * Creates an ArrayList of valid registers for both the source & destination registers
     */
    public void RegisterListCreator(){
        for(int i = 0; i < 14; i++){
            validSources.add("R" + i + ",");
            validDestination.add("R" + i);
        }
    }

}
