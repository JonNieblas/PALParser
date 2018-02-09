import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.*;
import java.util.*;

public class PALParser {

    private final String START = "STR";//valid opcode for beginning of .pal
    private final String END = "EXIT";//valid opcode for end of .pal
    private final int VALIDLABELLENGTH = 12;//max length of label
    private final int VALIDENDLENGTH = 4;//only length of end opcode
    private final int VALIDSTARTLENGTH = 3;//only length of start opcode

    private String line = null;//current line being parsed
    private String firstWord;//first word of each line (used to find opcode or label)
    private String fileName;// file to be named from main
    private int currentLine = 0;//used for error messaging to the user
    private int wordsInLine = 0;//counts words in a line

    private ArrayList<String> opList = new ArrayList<String>();//contains valid opcodes
    private ArrayList<String> labelList = new ArrayList<String>();//contains valid labels for branches
    private ArrayList<String> branchList = new ArrayList<String>();//contains valid branch opcodes
    private List<String> linesToLog = new ArrayList<String>();//lines to be printed

    public PALParser(ArrayList<String> opList,String file){
        this.opList = opList;
        this.fileName = file;
    }

    /*
     * Parses a file and prints each line (as of now);
     */
    public void Parser() {
        try {
            FileReader fileReader = new FileReader(fileName);
            LogHeader(linesToLog);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {//lines still left in .pal
                if(!line.isEmpty() || !line.trim().equals("")) {//if not an empty line
                    currentLine++;
                    SentenceSplitter(line);
                    linesToLog.add(currentLine + " " + line);
                    wordsInLine = 0;
                }
            }
            FileWriter(linesToLog);//give report via .log
            bufferedReader.close();//close process
        } catch (FileNotFoundException ex) {
            System.out.println(fileName + " doesn't exist");
        } catch (IOException ex) {
            System.out.println("error reading file " + fileName);
        }
    }

    /*
     * Parses a line and pin points the first word.
     * Will compare first word to a selection of opcodes
     * will continue to split the line, looking for commas/registers
     * Each source needs to be followed by a comma
     * possibly make sentence splitters for each operand
     */
    public void SentenceSplitter(String line) {
        String[] wordSplitter = line.split(" ");
        String currentWord = " ";

        for (String word : wordSplitter) {
            wordsInLine++;
            currentWord = word;
            if (wordsInLine == 1) {
                firstWord = currentWord;
                linesToLog.add("First word: " + firstWord);
            }
        }
    }

    /*
     * Takes strings, which will include statements and errors
     * then adds them to a .log file.
     */
    public void FileWriter(List<String> list){
        Path logFile = Paths.get(fileName + ".log");//used for writing to .log file
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
        String palName = fileName + ".pal";
        String logName = fileName + ".log";
        String strDate = dateFormat.format(date);
        String myName = "Jonathan Nieblas";
        String courseName = "CS 3210";

        String header = (title + " || " + palName + " || " + logName + " || " + strDate + " || "
                            + myName + " || " + courseName);
        list.add(header);
        list.add(" ");
    }
}