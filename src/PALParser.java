import java.io.BufferedReader;
import java.io.*;

public class PALParser {

    private String fileName = " ";// file to be named from main

    String line = null;
    int currentLine = 0; //used for error messaging to the user
    String firstWord;

    public PALParser(String file){
        this.fileName = file;
    }

    /*
     * Parses a file and prints each line (as of now);
     */
    public void Parser() {
        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                SentenceSplitter(line);
                currentLine++;
                System.out.println(currentLine + " " + line);//checks that we can print a file + line number
            }

            bufferedReader.close();
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
    public void SentenceSplitter(String line){
        String[] wordSplitter = line.split(" ");
        int wordCount = 0;

        for(String word : wordSplitter){
            wordCount++;
            if (wordCount == 1) {
                firstWord = word;
                System.out.println("firstWord: " + firstWord);
            }
            break;
        }
    }
}
