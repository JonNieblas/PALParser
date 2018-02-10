import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.*;
import java.util.*;

public class PALParser {

    Opcode opcode = new Opcode();//methods contained here will be

    private final String START = "SRT";//valid opcode for beginning of .pal
    private final String END = "END";//valid opcode for end of .pal
    private int startCounter, opCounter = 0;

    private String line = " ";//current line being parsed without comments
    private String firstWord, fileName, fileNameAppended, originalLine;
    private int currentLine = 1;//used for error messaging to the user
    private int wordsInLine = 0;//counts words in a line

    private ArrayList<String> opList;//contains valid opcodes
    private ArrayList<String> labelList= new ArrayList<>();//contains valid labels for branches
    private List<String> linesToLog = new ArrayList<>();//lines to be printed
    private ArrayList<Integer> numberOfErrors = new ArrayList<>();

    public PALParser(ArrayList<String> opList, String file){
        this.opList = opList;
        this.fileName = file;
        fileNameAppended = file.replace(".pal", "");
    }

    /*
     * Parses a file and prints each line (as of now);
     */
    public void Parser() {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            LogHeader(linesToLog);
            String comment;
            String newLine;

            while ((line = bufferedReader.readLine()) != null) {//lines still left in .pal
                if(line.contains(";") && line.lastIndexOf(';')-1 > -1) {//check for comments
                    comment = line.substring(line.lastIndexOf(';') - 1);//will take comment
                    newLine = line.replace(comment, "");//will remove comment from line
                }else{
                    newLine = line;
                }
                if(newLine.contains(";")){//line that only contains comment
                    linesToLog.add(currentLine + " " + newLine);
                    currentLine++;
                }else {
                    originalLine = line;
                    LabelOrOpcode(newLine);
                }
            }
            CheckLastLine();//see if last line is valid
            LogSummary(numberOfErrors);
            FileWriter(linesToLog);//writes log array to .log file
            bufferedReader.close();//close process
        } catch (FileNotFoundException ex) {
            System.out.println(fileName + " doesn't exist");
        } catch (IOException ex) {
            System.out.println("error reading file " + fileName);
        }
    }

    /*
     * Checks to see that PAL program's last opcode is "END"
     */
    public void CheckLastLine(){
        ErrorHandler err = new ErrorHandler(linesToLog);
        String lastLine = originalLine.trim();
        if(!lastLine.equals(END)){
            err.AddToErrorList(14);
            err.ErrorsToLog(numberOfErrors);
        }
    }

    /*
     * Parses a line and pin points the first word.
     * Will compare first word to a selection of opcodes
     * will continue to split the line, looking for commas/registers
     * Each source needs to be followed by a comma
     * possibly make sentence splitters for each operand
     */
    public void OpcodeChecker(String line, ErrorHandler err) {

        String[] wordSplitter = line.split(" ");
        for (String word : wordSplitter) {
            wordsInLine++;
            firstWord = word;
            if (wordsInLine == 1) {
                if(opList.contains(firstWord) && startCounter == 0){
                    err.AddToErrorList(13);
                    linesToLog.add(currentLine + " " + originalLine);
                    err.ErrorsToLog(numberOfErrors);
                    break;
                }else if(opList.contains(firstWord)){
                    if(opCounter == 0){
                        opCounter++;
                    }
                    opcode.OpcodeHandler(firstWord, line, linesToLog, currentLine, labelList, originalLine, numberOfErrors);
                    break;
                } else if(firstWord.equals(END)){
                    ENDHandler(line, err);
                    break;
                } else if(firstWord.equals(START)){
                    SRTHandler(line, err);
                    break;
                } else if(firstWord.equals("DEF")){
                    if(opCounter == 1){
                        err.AddToErrorList(15);
                        linesToLog.add(currentLine + " " + originalLine);
                        err.ErrorsToLog(numberOfErrors);
                        break;
                    }
                    else{
                        opcode.OpcodeHandler(firstWord, line, linesToLog, currentLine, labelList, originalLine, numberOfErrors);
                    }
                }
                else{
                    err.AddToErrorList(5);
                    err.AddToProblemWordList(word);
                    linesToLog.add(currentLine + " " + originalLine);
                    err.ErrorsToLog(numberOfErrors);
                    break;
                }
            }
        }
        wordsInLine = 0;//reset counter of words in a line
    }

    /*
     * Checks that SRT is used correctly
     */
    public void SRTHandler(String line, ErrorHandler err){
        String newLine = line.replace(" ", "");
        if(newLine.length() > 3){
            err.ENDOrSRT(line, 1, currentLine, linesToLog, numberOfErrors);
        } else if(startCounter == 1){
            err.AddToErrorList(11);
            linesToLog.add(currentLine + " " + line);
            err.ErrorsToLog(numberOfErrors);
        } else{
            linesToLog.add(currentLine + " " + originalLine);
            startCounter = 1;
        }
    }

    /*
     * Checks that SRT is used correctly
     */
    public void ENDHandler(String line, ErrorHandler err){
        String newLine = line.replace(" ", "");
        if(newLine.length() > 3){
            err.ENDOrSRT(line, 0, currentLine, linesToLog, numberOfErrors);
        } else if(startCounter == 0) {
            err.AddToErrorList(12);
            linesToLog.add(currentLine + " " + line);
            err.ErrorsToLog(numberOfErrors);
        }else{
            linesToLog.add(currentLine + " " + originalLine);
            startCounter = 0;
            opCounter = 0;
        }
    }

    /*
     * Checks if line is a label or a branch instruction
     */
    public void LabelChecker(String line, ErrorHandler err){
        if(line.contains("BEQ") || line.contains("BGT") || line.contains("BR")){
            OpcodeChecker(line, err);
        }else {
            err.AddToErrorList(4);
            err.AddToProblemWordList(line);
            linesToLog.add(currentLine + " " + originalLine);
            err.ErrorsToLog(numberOfErrors);
        }
    }
    /*
     * Checks that a line is not empty. If it is not, then it checks to see if the line
     * is a label or opcode.
     * If neither, it throws an error.
     */
    public void LabelOrOpcode(String line) {
        ErrorHandler err = new ErrorHandler(linesToLog);
        if (!line.isEmpty() || !line.trim().equals("")) {//if not an empty line
            if (line.endsWith(":") && line.length() < 12) {//is label
                labelList.add(line);
                linesToLog.add(currentLine + " " + line);
            } else if ((line.contains(":") && !line.endsWith(":")) || (line.contains(":") && line.length() > 11
                        || (line.contains(":") && line.contains(" ")))){
                LabelChecker(line, err);
            } else{
                OpcodeChecker(line, err);//split line to find first opcode
            }
            currentLine++;
        }
    }

    /*
     * Takes strings, which will include statements and errors
     * then adds them to a .log file.
     */
    public void FileWriter(List<String> list){
        Path logFile = Paths.get(fileNameAppended + ".log");//used for writing to .log file
        try {
            Files.write(logFile, list, Charset.forName("UTF-8"));
        }
        catch(IOException ex){
            System.out.println("Error writing line to " + logFile);
        }
    }

    /*
     * Adds header to linesToLog
     * Initially needed in Parser method
     */
    public void LogHeader(List<String> list){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        //components of header
        String title = "PAL Error Check";
        String palName = fileNameAppended;
        String logName = fileNameAppended + ".log";
        String strDate = dateFormat.format(date);
        String myName = "Jonathan Nieblas";
        String courseName = "CS 3210";

        String header = (title + " || " + palName + " || " + logName + " || " + strDate + " || "
                            + myName + " || " + courseName);
        list.add(header);
        list.add(" ");
        list.add("PAL Program Listing:");
        list.add(" ");
    }

    /*
     * Counts each individual error type present.
     * Reports a summary of the total count of errors, and how many of each type
     * were present (if at all).
     */
    public void LogSummary(ArrayList<Integer> numberOfErrors){
        int err0 = 0, err1 = 0, err2 = 0, err3 = 0, err4 = 0, err5 = 0, err6 = 0, err7 = 0,
                err8 = 0, err9 = 0, err10 = 0, err11 = 0, err12 = 0, err13 = 0, err14 = 0, err15 = 0;
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
            }
        }
        int totalErrors = err0 + err1 + err2 + err3 + err4 + err5 + err6 + err7 + err8 + err9 + err10 + err11
                            + err12 + err13 + err14 + err15;

        linesToLog.add(" ");
        linesToLog.add("Summary ----------");
        linesToLog.add(" ");
        linesToLog.add("total Errors = " + totalErrors);

        if(err0 > 0){
            linesToLog.add(" " + err0 + " " + "Wrong Operand Type(s)");
        } if(err1 > 0){
            linesToLog.add(" " + err1 + " " + "Ill-Formed Operand(s)");
        } if(err2 > 0){
            linesToLog.add(" " + err2 + " " + "Too Many Operands");
        } if(err3 > 0){
            linesToLog.add(" " + err3 + " " + "Too Few Operands");
        } if(err4 > 0){
            linesToLog.add(" " + err4 + " " + "Ill-Formed Label(s)");
        } if(err5 > 0){
            linesToLog.add(" " + err5 + " " + "Invalid Opcode(s)");
        } if(err6 > 0){
            linesToLog.add(" " + err6 + " " + "Branches to Non-Existent Label(s)");
        } if(err7 > 0){
            linesToLog.add(" " + err7 + " " + "Wrong Operand Type(s)");
        } if(err8 > 0){
            linesToLog.add(" " + err8 + " " + "Ill-Formed Exit Opcode(s)");
        } if(err9 > 0){
            linesToLog.add(" " + err9 + " " + "Ill-Formed Start Opcode(s)");
        } if(err10 > 0){
            linesToLog.add(" " + err10 + " " + "Missing Label(s) in Branch(es)");
        } if(err11 > 0){
            linesToLog.add(" " + err11 + " " + "Misplaced SRT(s)");
        } if(err12 > 0){
            linesToLog.add(" " + err12 + " " + "Misplaced END(s)");
        } if(err13 > 0){
            linesToLog.add(" " + err13 + " " + "Code(s) Outside of Program");
        } if(err14 > 0){
            linesToLog.add(" " + err14 + " " + "END Not Detected");
        } if(err15 > 0){
            linesToLog.add(" " + err15 + " " + "Invalid DEF(s)");
        } if(totalErrors > 0){
            linesToLog.add("Processing Complete - PAL program is not valid.");
        } else{
            linesToLog.add("Processing Complete - PAL program checks out.");
        }
    }
}