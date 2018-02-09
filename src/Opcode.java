/*
 * Opcode contains all methods dealing with opcodes, as well as the construction of the opList arrayList
 */

import java.util.ArrayList;
import java.util.List;

public class Opcode {

    private ArrayList<String> validSources = new ArrayList<String>();
    private ArrayList<String> validDestinations = new ArrayList<String>();
    String[] wordSplitter;

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
    public void OpcodeHandler(String opcode, String line, List<String> linesToLog, int lineCount){
        if(opcode.equals("ADD") || opcode.equals("SUB") || opcode.equals("MUL") || opcode.equals("DIV")){
            ASMDOpcode(line, linesToLog, lineCount);
        }
        else if(opcode.equals("COPY")){
            COPYOpcode(line, linesToLog, lineCount);
        }
        else if(opcode.equals("MOVE")){
            MOVEOpcode(line, linesToLog, lineCount);
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
     * Observes an ASMD line to make sure there are no errors.
     * Reports errors to ErrorHandler.
     */
    public void ASMDOpcode(String line, List<String> linesToLog, int lineCount){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");

        for (String word : wordSplitter){
            if((!validSources.contains(word) && wordCount > 0 && wordCount < 3)
                    || (!validDestinations.contains(word) && wordCount == 3 && !validSources.contains(word))){
                word = word.replace(",", "");
                StringOrInteger(err, word);
            }
            wordCount++;
        }

        WordsInLine(err, wordCount, "ASMD");
        linesToLog.add(lineCount + " " + line);
        err.ErrorsToLog();//writes errors to .log file
    }

    public void COPYOpcode(String line, List<String> linesToLog, int lineCount){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if((wordCount == 1 && !validSources.contains(word)) || (wordCount == 2 && !validDestinations.contains(word)
                    && !validSources.contains(word))){
                StringOrInteger(err, word);
            }
            wordCount++;
        }

        WordsInLine(err, wordCount, "MC");
        linesToLog.add(lineCount + " " + line);
        err.ErrorsToLog();
    }

    public void MOVEOpcode(String line, List<String> linesToLog, int lineCount){

    }

    /*
     * Creates an ArrayList of valid registers for both the source & destination registers
     */
    public void RegisterListCreator(){
        for(int i = 0; i < 14; i++){
            validSources.add("R" + i + ",");
            validDestinations.add("R" + i);
        }
    }

    /*
     * Checks to see if the incorrect operand is an immediate value or an ill-formed operand.
     * Second comparison in if statement catches word with comma (EX: R1,; 22,; wow,;
     */
    public void StringOrInteger(ErrorHandler err, String word){
        if(word.matches("^-?\\d+$") || word.matches("^-?\\d+.$")){
            err.AddToErrorList(0);//error for immediate value
            err.AddToProblemWordList(word);
        }
        else {
            err.AddToErrorList(1);//error for ill-formed operand
            err.AddToProblemWordList(word);
        }
    }

    /*
     * If there are more or less words in a line then there should be,
     * this will alert the ErrorHandler.
     */
    public void WordsInLine(ErrorHandler err, int wordCount, String opcode){
        if(opcode == "ASMD") {
            if (wordCount > 4) {
                err.AddToErrorList(2);
            }
            if (wordCount < 4) {
                err.AddToErrorList(3);
            }
        }
        if(opcode == "MC"){
            if (wordCount > 3) {
                err.AddToErrorList(2);
            }
            if (wordCount < 3) {
                err.AddToErrorList(3);
            }
        }
    }

}
