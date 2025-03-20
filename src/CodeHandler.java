import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeHandler {

    //Private String to hold the document(BASIC FILE)
    private String document;

    //Private integer(index) to hold the finger position.
    private int index;

    //Constructor where the filename is taken
    public CodeHandler(String filename) {

        document = filename;
        index = 0;
        try {
            //Read the content of the file into a String variable
            Path path = Paths.get(filename);
            this.document = new String(Files.readAllBytes(path));
            this.index = 0;

        }
        catch(Exception e) {
            //Handles exceptions
            e.printStackTrace();

        }

    }


    //Method that looks “i” characters ahead and returns that character and doesn’t move the index
    public char Peek(int i) {
        if(index + i < document.length()) {
            return document.charAt(index + i);
        }
        else {
            return '\0';
        }
    }

    //Method that returns a string of the next “i” characters but doesn’t move the index
    public String peekString(int i) {
        if (index < document.length() && i > 0) {
            int endIndex = Math.min(index + i, document.length());
            return document.substring(index, endIndex);
        } else {
            return null;
        }
    }

    //Method that returns the next character and moves the index
    public char getChar() {
        if(index < document.length()) {
            char c;
            c = document.charAt(index);
            index++;
            return c;
        }
        else {
            return '\0';
        }
    }

    //Method that moves the index ahead “i” positions
    public void Swallow(int i) {
        index += i;
    }

    //Method that returns true if we are at the end of the document

    public boolean isDone() {
        if(index >= document.length()){
            return true;
        }
        else {
            return false;
        }
    }

    //Method that returns the rest of the document as a string
    public String Remainder() {
        if(index < document.length()) {
            return document.substring(index);

        }else {
            return null;
        }

    }
}
