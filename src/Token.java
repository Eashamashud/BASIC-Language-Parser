
//Token Class

import java.util.LinkedList;

public class Token {

    //Enum of TokenTypes
    public enum TokenType { WORD, NUMBER,ENDOFLINE, PRINT, READ, INPUT, DATA, GOSUB, FOR,
        TO , STEP, NEXT , RETURN , IF, THEN , FUNCTION, WHILE, END , EQUALS, PLUS, MINUS, MULTIPLY,
        DIVIDE, LESSTHAN_EQUALTO, GREATERTHAN_EQUALTO, NOTEQUALS , LPAREN, RPAREN, STRINGLITERAL, LABEL ,
        LESS_THAN, GREATER_THAN, EQUAL,  MODULO, COMMA , COLON, SINGLE_QUOTE, IDENTIFIER, ELSE, RANDOM, LEFT, RIGHT, MID, NUM, VAL, SEMICOLON, FLOAT};

    private TokenType type;
    private String value;
    private int lineNumber;
    private int charPosition;

    // Constructor for tokentype, line number, and position
    public Token(TokenType type,  int lineNumber , int charPosition) {
        this.type = type;
        this.value = "";
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;

    }

    // Constructor for tokentype, line number, position, and value
    public Token(TokenType type, String value ,int lineNumber, int charPosition ) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;

    }

    // toString method to output the token type and the value
    @Override
    public String toString() {
        if (value!= null) {
            return  type + " (" + value + ")" ;
        }
        else {
            return type.toString();
        }}


    //Getter for token type
    public TokenType getType() {
        return type;
    }

    // Getter for token value
    public String getValue() {
        return value;
    }

    // Getter for line number
    public int getLineNumber() {
        return lineNumber;
    }

    // Getter for character position
    public int getCharPosition() {
        return charPosition;
    }





}

