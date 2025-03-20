import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;


public class Lexer {


    private int lineNumber; // Current line number being processed
    private int linePosition; // Current position in the line being processed
    private CodeHandler codeHandler;  // Handles the code input

    // Map of known keywords to their token types
    private HashMap<String, Token.TokenType> knownWords;
    // Map of two-character symbols to their token types
    private HashMap<String, Token.TokenType> twoCharSymbols;
    // Map of one-character symbols to their token types
    private HashMap<String, Token.TokenType> oneCharSymbol;

    private HashMap<String, Token.TokenType> populateSymbol;

    // Lexer Constructor
    public Lexer() {
        knownWords = new HashMap<>();
        populateknownWords();// Initialize and populate the map of known words

        twoCharSymbols = new HashMap<>();
        populatetwoCharSymbols(); // Initialize and populate the map of two-character symbols

        oneCharSymbol = new HashMap<>();
        populateoneCharSymbol();  // Initialize and populate the map of one-character symbols

        //populateSymbol = new HashMap<>();
        populateFunction();

    }

    //Method to populate the map of known words
    private void populateknownWords() {
        knownWords.put("PRINT", Token.TokenType.PRINT);
        knownWords.put("READ", Token.TokenType.READ);
        knownWords.put("INPUT", Token.TokenType.INPUT);
        knownWords.put("DATA", Token.TokenType.DATA);
        knownWords.put("GOSUB", Token.TokenType.GOSUB);
        knownWords.put("FOR", Token.TokenType.FOR);
        knownWords.put("TO", Token.TokenType.TO);
        knownWords.put("STEP", Token.TokenType.STEP);
        knownWords.put("NEXT", Token.TokenType.NEXT);
        knownWords.put("RETURN", Token.TokenType.RETURN);
        knownWords.put("IF", Token.TokenType.IF);
        knownWords.put("THEN", Token.TokenType.THEN);
        knownWords.put("FUNCTION", Token.TokenType.FUNCTION);
        knownWords.put("WHILE", Token.TokenType.WHILE);
        knownWords.put("END", Token.TokenType.END);

    }

    // Method to populate the map of two-character symbols
    private void populatetwoCharSymbols() {

        twoCharSymbols.put("<=", Token.TokenType.LESSTHAN_EQUALTO);
        twoCharSymbols.put(">=", Token.TokenType.GREATERTHAN_EQUALTO);
        twoCharSymbols.put("<>", Token.TokenType.NOTEQUALS);
        twoCharSymbols.put("==", Token.TokenType.EQUALS);
    }


    // Method to populate the map of one-character symbols
    private void populateoneCharSymbol() {

        oneCharSymbol.put("=", Token.TokenType.EQUAL);
        oneCharSymbol.put("+", Token.TokenType.PLUS);
        oneCharSymbol.put("-", Token.TokenType.MINUS);
        oneCharSymbol.put("*", Token.TokenType.MULTIPLY);
        oneCharSymbol.put("/", Token.TokenType.DIVIDE);
        oneCharSymbol.put("=", Token.TokenType.EQUALS);
        oneCharSymbol.put("(", Token.TokenType.LPAREN);
        oneCharSymbol.put(")", Token.TokenType.RPAREN);
        oneCharSymbol.put("<", Token.TokenType.LESS_THAN);
        oneCharSymbol.put(">", Token.TokenType.GREATER_THAN);
        oneCharSymbol.put("%", Token.TokenType.MODULO);
        oneCharSymbol.put(",", Token.TokenType.COMMA);
        oneCharSymbol.put(":", Token.TokenType.COLON);
        oneCharSymbol.put("'", Token.TokenType.SINGLE_QUOTE);


    }

    private void populateFunction(){
        knownWords.put("RANDOM", Token.TokenType.FUNCTION);
        knownWords.put("LEFT$", Token.TokenType.FUNCTION);
        knownWords.put("RIGHT$", Token.TokenType.FUNCTION);
        knownWords.put("MID$", Token.TokenType.FUNCTION);
        knownWords.put("NUM$", Token.TokenType.FUNCTION);
        knownWords.put("VAL%", Token.TokenType.FUNCTION);

    }

    //Reads the BASIC file and tokenizes it
    public LinkedList<Token> lex(String filename) {


        this.codeHandler = new CodeHandler(filename);
        this.lineNumber = 1;
        this.linePosition = 0;


        //List to store generated tokens
        LinkedList<Token> tokens = new LinkedList<>();

        //Iterate through the characters in the BASIC file
        while (!codeHandler.isDone()) {
            // The next character is peeked at
            char currentChar = codeHandler.Peek(0);


            // If the character is a space or tab, we will just move past it (increment position).
            if (Character.isWhitespace(currentChar)) {

                //If the character is a linefeed (\n), we will create a new EndOfLine token with no “value” and add it to token list.
                //We should also increment the line number and set line position to 0.
                if (currentChar == '\n') {
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, lineNumber, linePosition));

                    lineNumber++;
                    linePosition = 0;

                }
                linePosition++;
                codeHandler.Swallow(1); // Move past linefeed


                //If the character is a carriage return (\r), we will ignore it.
            } else if (currentChar == '\r') {
                //Ignore carriage return
                codeHandler.Swallow(1);
            }

            //If the character is a letter, we need to call ProcessWord and add the result to our list of tokens.
            else if (Character.isLetter(currentChar)) {
                tokens.add(processWord());
            }

            //If the character is a digit, we need to call ProcessDigit and add the result to our list of tokens.
            else if (Character.isDigit(currentChar)) {
                tokens.add(processDigit());

                // If the character is a recognized one-character symbol, call ProcessSymbol and add the result to the list of tokens.
            } else if (oneCharSymbol.containsKey(Character.toString(currentChar))) {
                tokens.add(processSymbol());

                // If the character is a double quote, call HandleStringLiteral and add the result to the list of tokens.
            } else if (currentChar == '"') {
                tokens.add(handleStringLiteral());
            }


            // Exception is thrown when encountering a character not recognized
            else {
                throw new RuntimeException("Unrecognized character: " + currentChar);
            }
        }
        // Add an EndOfLine token for the last line
        tokens.add(new Token(Token.TokenType.ENDOFLINE, lineNumber, linePosition));
        // Return the list of tokens
        return tokens;
    }


    //Method to process words
    private Token processWord() {

        // Store the starting line and position for creating tokens
        int startLine = lineNumber;
        int startPosition = linePosition;


        // Used to accumulate characters of the word
        StringBuilder wordBuilder = new StringBuilder();


        // Check if the current character is a letter, digit, or underscore
        while (!codeHandler.isDone() && Character.isLetterOrDigit(codeHandler.Peek(0)) || codeHandler.Peek(0) == '_') {
            wordBuilder.append(codeHandler.getChar());
            // Position value is incremented
            linePosition++;
        }

        // Check for special characters '$' or '%' at the end of a word
        char specialChar = codeHandler.Peek(0);
        if (specialChar == '$' || specialChar == '%') {
            wordBuilder.append(codeHandler.getChar());
            linePosition++;
        }

        // A WORD token is created with the accumulated value
        String word = wordBuilder.toString();

        // Check for ':' and create a LABEL token
        if (codeHandler.Peek(0) == ':') {
            codeHandler.getChar(); // Consume ':'
            return new Token(Token.TokenType.LABEL, word, lineNumber, startPosition);
            // Assuming position after ':'
        } else {
            // If ':' is not present, create a WORD token
            if (knownWords.containsKey(word))
                return new Token(knownWords.get(word),word,lineNumber,startPosition);
            //return new Token(Token.TokenType.WORD)
            return new Token(Token.TokenType.WORD, word, lineNumber, startPosition);
        }

    }


private Token processDigit() {
    // Store the starting line and position for creating tokens
    int startLine = lineNumber;
    int startPosition = linePosition;

    // Used to accumulate numbers of the word
    StringBuilder numberBuilder = new StringBuilder();

    // Flag to check if a dot has been encountered
    boolean dotEncountered = false;

    // Check if the current character is a digit, or a dot.
    while (!codeHandler.isDone() && (Character.isDigit(codeHandler.Peek(0)) || codeHandler.Peek(0) == '.')) {
        char currentChar = codeHandler.getChar();
        if (currentChar == '.' && dotEncountered) {
            // If dot is encountered again, it's not a valid number
            // Handle error or break the loop
            break;
        }
        if (currentChar == '.') {
            dotEncountered = true;
        }
        numberBuilder.append(currentChar);
        linePosition++;
    }

    // Create a NUMBER token based on whether a dot was encountered
    String number = numberBuilder.toString();
    if (dotEncountered) {
        return new Token(Token.TokenType.FLOAT, number, startLine, startPosition);
    } else {
        return new Token(Token.TokenType.NUMBER, number, startLine, startPosition);
    }
}

    // Method to handle string literals
    private Token handleStringLiteral() {
        // StringBuilder to accumulate characters of the string literal
        StringBuilder literal = new StringBuilder();

        // Consume the opening double quote
        codeHandler.getChar();
        //Reads characters until a matching double quote is found
        while (!codeHandler.isDone() && (codeHandler.Peek(0) != '"')) {
            literal.append(codeHandler.getChar());
        }

        //Consume the closing double quote
        codeHandler.getChar();

        // Convert StringBuilder to String to get the string literal value
        String stringLiteral = literal.toString();
        // Create a STRINGLITERAL token with the accumulated value
        return new Token(Token.TokenType.STRINGLITERAL, stringLiteral, lineNumber, linePosition);

    }


    // Method to process symbols
    private Token processSymbol() {

        // String representing the one-character symbol
        String oneCharSymboL = codeHandler.peekString(0);

        // String representing the next character in the input
        String peek;
        peek = Character.toString(codeHandler.Peek(1));

        // String representing the combined symbol (current character + next character)
        String symbol;
        char currentChar = codeHandler.getChar();
        symbol = Character.toString(currentChar);
        linePosition++;

        // Check if the combined symbol is a recognized two-character symbol
        if (twoCharSymbols.containsKey(symbol + peek)) {
            codeHandler.getChar(); // Consume the next character
            // Create a token for the recognized two-character symbol
            return new Token(twoCharSymbols.get(symbol + peek), lineNumber, linePosition);
        } else if (oneCharSymbol.containsKey(symbol)) {
            // If not a two-character symbol, check if it's a recognized one-character symbol
            return new Token(oneCharSymbol.get(symbol), lineNumber, linePosition);
        }

        // If no match is found, throw an exception or handle accordingly
        throw new RuntimeException("Unrecognized symbol at line " + lineNumber + ", position " + linePosition);

    }

}