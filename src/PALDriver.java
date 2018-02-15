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
        String file3 = "PAL Program 1.pal";
        String file4 = "PAL Program 2.pal";

        PALParser parser1 = new PALParser (file);
        PALParser parser2 = new PALParser (file1);
        PALParser parser3 = new PALParser (file2);
        PALParser parser4 = new PALParser (file3);
        PALParser parser5 = new PALParser (file4);
        parser1.Parser();
        parser2.Parser();
        parser3.Parser();
        parser4.Parser();
        parser5.Parser();
    }
}
