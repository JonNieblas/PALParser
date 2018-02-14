import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.*;
import java.util.*;
/**
 * Class PALParser contains void method Parser that reads a PAL assembly language
 * file, breaks it down originalLine by originalLine, passing it to a series of handlers both in
 * this class, Class Opcode, and Class ErrorHandler in order to check syntax correctness.
 * @author Jonathan Nieblas
 */
public class PALParser {
    /** Handles a line containing a valid opcode, excluding SRT/END. */
    Opcode opcode = new Opcode();

    /** Valid opcode to begin a program with. */
    private final String FIRST_OPCODE_NAME = "SRT";//valid opcode for beginning of .pal
    /** Valid opcode to end a program with. */
    private final String LAST_OPCODE_NAME = "END";//valid opcode for end of .pal

    /** Counters signaling program beginning/end. */
    private int startCounter;
    /** Counter signaling when DEF opcode can be used. */
    private int opCounter;
    /** Current originalLine num of the .pal file. */
    private int currentLine = 1;//used for error messaging to the user
    /** Count of the words in a originalLine, for finding first word. */
    private int wordsInLine = 0;//counts words in a originalLine

    /** Line to be parsed from .pal file. Will contain a label w/ no comments*/
    private String originalLine = " ";
    /** Line with comments & other extras (such as spaces) removed. */
    private String newLine = " ";
    /** Last non-comment line of a .pal file. */
    private String lastLine = " ";
    /** Place holder for comment; will be removed from originalLine. */
    private String comment = " ";
    /** First word of a originalLine. Helps to separate Opcode from originalLine. */
    private String firstWord;

    /** Name of .pal file to be parsed with extension. */
    private String fileName;
    /** File name with .pal extension removed. */
    private String fileNameAppended;

    /** Contains all valid opcodes. */
    private ArrayList<String> opList = new ArrayList<>();//contains valid opcodes
    /** Stores all valid labels for branches. */
    private ArrayList<String> labelList = new ArrayList<>();//contains valid labels for branches
    /** Contains lines to be added to .log file. */
    private List<String> linesToLog = new ArrayList<>();//lines to be printed
    /** Contains each error in .pal file to be added up at the end. */
    private ArrayList<Integer> numberOfErrors = new ArrayList<>();

    /** Counters for num of each error in .pal file.*/
    private int err0 = 0, err1 = 0, err2 = 0, err3 = 0, err4 = 0, err5 = 0, err6 = 0, err7 = 0,
            err8 = 0, err9 = 0, err10 = 0, err11 = 0, err12 = 0, err13 = 0, err14 = 0, err15 = 0, err16 = 0;

    /**
     * Takes a given list of opcodes and a .pal file and assigns
     * them accordingly.
     * @param file to be parsed, given by String fileName;
     */
    public PALParser(String file){
        this.fileName = file;
        CreateOpList();
        fileNameAppended = file.replace(".pal", "");

    }

    /**
     * Parses a .pal file, passes lines to handlers, and writes
     * to .log when finished.
     */
    public void Parser() {
        try {
            FileReader fileReader = new FileReader("pal/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            LogHeaderWriter();

            //LINE PARSER
            while ((originalLine = bufferedReader.readLine()) != null) {
                CommentHandler();
            }
            LastLineHandler();
            LabelsNotUsed();
            LogSummaryWriter();
            FileWriter();
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(fileName + " could not be found.");
        } catch (IOException ex) {
            System.out.println("Could not read file: " + fileName + ".");
        }
    }

    /**
     * Checks if an originalLine contains a comment or is just a comment.
     */
    public void CommentHandler(){
        if(originalLine.contains(";")) {//check for comments
            comment = originalLine.substring(originalLine.indexOf(";"));
            newLine = originalLine.replace(comment, "");//will remove comment from originalLine
            originalLine = newLine.trim();
        } else{
            newLine = originalLine.trim();
            originalLine = originalLine.trim();
        } if(!newLine.isEmpty()){
            lastLine = originalLine.trim();
            LabelOrOpcodePasser();
        }
    }

    /**
     * Checks if the line contains a label, or if it is just an opcode.
     */
    public void LabelOrOpcodePasser() {
        ErrorHandler err = new ErrorHandler(linesToLog);
        newLine = newLine.trim();
        if (newLine.contains(":")) {
            if(startCounter == 1) {
                LabelHandler(err);
            } else{
                err.ErrorStatementPreparer(13, " ", linesToLog, currentLine, originalLine, numberOfErrors);
            }
        } else {
            OpcodeHandler(err, newLine);
        }
        currentLine++;
    }

    /**
     * Finds the label in a .pal line and saves it.
     * Passes a line containing the opcode statement to
     * OpcodeHandler();
     * @param err reported to if any errors
     */
    public void LabelHandler(ErrorHandler err){
        String opCode, label;
        originalLine = newLine;
        opCode = newLine.substring(newLine.indexOf(":") + 1).trim();
        label = newLine.replace(opCode, "");
        label = label.replace(":", "").trim();
        if(!opCode.isEmpty() || !opCode.equals("")) {
            labelList.add(label);
            OpcodeHandler(err, opCode);
        } else {
            labelList.add(label);
            linesToLog.add(currentLine + " " + newLine);
        }
    }

    /**
     * Takes first word of originalLine and finds if it's a
     * valid opcode. Passes to SpecialOpcodeHandler() if
     * opcode is SRT/END/DEF or not recognized.
     * @param err reported to if any errors
     */
    public void OpcodeHandler(ErrorHandler err, String newLine) {

        String[] wordSplitter = newLine.split(" ");
        for (String word : wordSplitter) {
            firstWord = word;
            if (wordsInLine == 0) {
                if(opList.contains(firstWord) && startCounter == 0){//catches opcode before .pal program begins
                    err.ErrorStatementPreparer(13, " ", linesToLog, currentLine, originalLine, numberOfErrors);
                    break;
                } else if(opList.contains(firstWord)){//passes lines with valid opcodes to Class Opcode
                    if(opCounter == 0){
                        opCounter++;
                    }
                    opcode.OpcodeMethodHandler(firstWord, newLine, linesToLog, currentLine, numberOfErrors, originalLine);
                    break;
                } else{
                    SpecialOpcodeHandler(err, word, newLine);
                    break;
                }
            }
            wordsInLine++;
        }
        wordsInLine = 0;
    }

    /**
     * Handles the validity of special opcodes SRT/END/DEF.
     * Also handles unrecognized opcodes and sends errors to ErrorHandler.
     * @param err reported to if any errors
     * @param specOp tested for validity
     */
    public void SpecialOpcodeHandler(ErrorHandler err, String specOp, String newLine){
        switch(firstWord){
            case LAST_OPCODE_NAME: ENDHandler(err, newLine);
                break;
            case FIRST_OPCODE_NAME: SRTHandler(err, newLine);
                break;
            case "DEF": if(opCounter == 1){
                err.ErrorStatementPreparer(15, " ", linesToLog, currentLine, originalLine, numberOfErrors);

            } else{
                opcode.OpcodeMethodHandler(firstWord, newLine, linesToLog, currentLine, numberOfErrors, originalLine);
            }
                break;
            default: if(startCounter == 0){
                err.ErrorStatementPreparer(13, " ", linesToLog, currentLine, originalLine, numberOfErrors);
            } else {
                err.ErrorStatementPreparer(5, specOp, linesToLog, currentLine, originalLine, numberOfErrors);
            }
                break;
        }
    }

    /**
     * Checks that use of SRT opcode is valid.
     * @param err reported to if any errors
     */
    public void SRTHandler(ErrorHandler err, String newLine){
        String thisLine = newLine.trim();
        if(thisLine.length() > 3){
            err.IncorrectENDOrSRTHandler(originalLine, 1, currentLine, linesToLog, numberOfErrors, thisLine);
        } else if(startCounter == 1){
            err.ErrorStatementPreparer(11, " ", linesToLog, currentLine, originalLine, numberOfErrors);
        } else{
            linesToLog.add(currentLine + " " + originalLine);
            startCounter = 1;
        }
    }

    /**
     * Checks that use of END opcode is valid.
     * @param err reported to if any errors
     */
    public void ENDHandler(ErrorHandler err, String newLine){
        String thisLine = newLine.trim();
        if(thisLine.length() > 3){
            err.IncorrectENDOrSRTHandler(originalLine, 0, currentLine, linesToLog, numberOfErrors, thisLine);
        } else if(startCounter == 0) {
            err.ErrorStatementPreparer(12, " ", linesToLog, currentLine, originalLine, numberOfErrors);
        } else{
            linesToLog.add(currentLine + " " + originalLine);
            startCounter = 0;
            opCounter = 0;
        }
    }

    /**
     * Checks to see that .pal file's last non-comment line is
     * equal to END opcode.
     */
    public void LastLineHandler(){
        ErrorHandler err = new ErrorHandler(linesToLog);
        if(lastLine.contains(";")) {
            comment = lastLine.substring(lastLine.indexOf(";"));
        }
        if(lastLine.contains(comment)){
            lastLine = lastLine.replace(comment, "");
            lastLine = lastLine.replace(" ", "");
        }
        if(lastLine.contains(":")){
            lastLine = lastLine.substring(lastLine.indexOf(":") + 1).trim();
        }
        if(!lastLine.equals(LAST_OPCODE_NAME)){
            err.ErrorStatementPreparer(14, lastLine, linesToLog, currentLine, " ", numberOfErrors);

        }
    }

    /**
     * Takes two lists, one containing valid labels from the program, another containing labels
     * that were found in branch instructions, and prepares them to be passed to the LabelErrorHandler
     * in Class ErrorHandler. This checks to see if there are any unused labels or any labels that don't
     * exist but were used in branch instructions.
     */
    public void LabelsNotUsed(){
        ErrorHandler err = new ErrorHandler(linesToLog);
        ErrorHandler err1 = new ErrorHandler(linesToLog);
        ArrayList<String> encounteredLabels = opcode.getEncounteredLabels();
        ArrayList<String> labelsFound = new ArrayList<>();

        labelsFound.addAll(encounteredLabels);
        encounteredLabels.removeAll(labelList);//labels that don't exist
        labelList.removeAll(labelsFound);//labels we didn't use

        err.LabelErrorHandler(labelList, numberOfErrors, 16);//check for any unused labels
        err1.LabelErrorHandler(encounteredLabels, numberOfErrors, 6);//check for any non-existent labels
    }

    /**
     * Takes an ArrayList containing all lines, errors & comments,
     * then writes it to a .log file named after the original .pal
     * file name.
     */
    public void FileWriter(){
        Path logFile = Paths.get("logs/" + fileNameAppended + ".log");//used for writing to .log file
        try {
            Files.write(logFile, linesToLog, Charset.forName("UTF-8"));
        }
        catch(IOException ex){
            System.out.println("Error writing originalLine to " + logFile);
        }
    }

    /**
     * Adds correct header information to ArrayList linesToLog.
     */
    public void LogHeaderWriter(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        String title = "PAL Error Check";
        String palName = fileNameAppended;
        String logName = fileNameAppended + ".log";
        String strDate = dateFormat.format(date);
        String myName = "Jonathan Nieblas";
        String courseName = "CS 3210";

        String header = (title + " || " + palName + " || " + logName + " || " + strDate + " || "
                            + myName + " || " + courseName);
        linesToLog.add(header);
        linesToLog.add(" ");
        linesToLog.add("PAL Program Listing:");
        linesToLog.add(" ");
    }

    /**
     * Adds a summary to the .log file after a .pal program
     * has been parsed.
     */
    public void LogSummaryWriter(){

        int totalErrors = CountTotalErrors();
        linesToLog.add(" ");
        linesToLog.add("Summary ----------");
        linesToLog.add(" ");
        linesToLog.add("total Errors = " + totalErrors);
        TotalErrorsToLog();
                if(totalErrors > 0){
            linesToLog.add("Processing Complete - PAL program is invalid.");
        } else{
            linesToLog.add("Processing Complete - PAL program is valid.");
        }
    }

    /**
     * Counts the number of each type of error in the .pal file.
     * Also, adds up the total number of errors.
     * @return total num of errors in .pal file
     */
    public int CountTotalErrors(){
        for(int i : numberOfErrors){
            switch(i){
                case 0: err0++;
                    break;
                case 1: err1++;
                    break;
                case 2: err2++;
                    break;
                case 3: err3++;
                    break;
                case 4: err4++;
                    break;
                case 5: err5++;
                    break;
                case 6: err6++;
                    break;
                case 7: err7++;
                    break;
                case 8: err8++;
                    break;
                case 9: err9++;
                    break;
                case 10: err10++;
                    break;
                case 11: err11++;
                    break;
                case 12: err12++;
                    break;
                case 13: err13++;
                    break;
                case 14: err14++;
                    break;
                case 15: err15++;
                    break;
                case 16: err16++;
                    break;
            }
        }
        int total = err0 + err1 + err2 + err3 + err4 + err5 + err6 + err7 + err8 + err9 + err10 + err11
                + err12 + err13 + err14 + err15 + err16;
        return total;
    }

    /**
     * Prints total number of each type of error to summary
     * if any errors are present at all.
     */
    public void TotalErrorsToLog(){
        if(err0 > 0){
            linesToLog.add(" " + err0 + " Wrong Operand Type(s) (Registers/Variables)");
        } if(err1 > 0){
            linesToLog.add(" " + err1 + " Ill-Formed Operand(s)");
        } if(err2 > 0){
            linesToLog.add(" " + err2 + " Too Many Operands");
        } if(err3 > 0){
            linesToLog.add(" " + err3 + " Too Few Operands");
        } if(err4 > 0){
            linesToLog.add(" " + err4 + " Ill-Formed Label(s)");
        } if(err5 > 0){
            linesToLog.add(" " + err5 + " Invalid Opcode(s)");
        } if(err6 > 0){
            linesToLog.add(" " + err6 + " Branches to Non-Existent Label(s)");
        } if(err7 > 0){
            linesToLog.add(" " + err7 + " Wrong Operand Type(s) (Immediate Values)");
        } if(err8 > 0){
            linesToLog.add(" " + err8 + " Ill-Formed Exit Opcode(s)");
        } if(err9 > 0){
            linesToLog.add(" " + err9 + " Ill-Formed Start Opcode(s)");
        } if(err10 > 0){
            linesToLog.add(" " + err10 + " Missing Label(s) in Branch(es)");
        } if(err11 > 0){
            linesToLog.add(" " + err11 + " Misplaced SRT(s)");
        } if(err12 > 0){
            linesToLog.add(" " + err12 + " Misplaced END(s)");
        } if(err13 > 0){
            linesToLog.add(" " + err13 + " Code(s) Outside of Program");
        } if(err14 > 0){
            linesToLog.add(" " + err14 + " END Not Detected");
        } if(err15 > 0){
            linesToLog.add(" " + err15 + " Invalid DEF(s)");
        } if(err16 > 0){
            linesToLog.add(" " + err16 + " Label(s) Not Used Warning");
        }
    }

    /**
     * Creates opList, containing valid opcodes.
     */
    public void CreateOpList(){
        opList.add("ADD");
        opList.add("SUB");
        opList.add("MUL");
        opList.add("DIV");
        opList.add("COPY");
        opList.add("MOVE");
        opList.add("INC");
        opList.add("DEC");
        opList.add("BEQ");
        opList.add("BGT");
        opList.add("BR");
    }
}