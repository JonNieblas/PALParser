/**
 * Class PALDriver for Class PALParser.
 * Contains the name of the .pal program to be parsed.
 * @author Jonathan Nieblas
 */
public class PALDriver {
    public static void main(String args[]){

        //files to be parsed here
        String file = "practice_program.pal";
        String file1 = "Increment.pal";
        String file2 = "PAL Test Programs.pal";

        PALParser parser1 = new PALParser (file);
        PALParser parser2 = new PALParser (file1);
        PALParser parser3 = new PALParser (file2);
        parser1.Parser();
        parser2.Parser();
        parser3.Parser();
    }
}
