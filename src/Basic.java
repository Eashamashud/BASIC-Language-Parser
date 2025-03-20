import java.util.*;


public class Basic {
    public static void main(String[] args) {
        try {
            //Accepts only one argument
            if (args.length != 1) {
                System.out.println("Error: Please provide exactly one argument (filename).");
                System.exit(1);
            }
            //Get the filename from the command-line arguments
            String filename = args[0];
            // Create an instance of the Lexer class
            Lexer lexer = new Lexer();
            LinkedList<Token> tokens = lexer.lex(filename);
            for (Token token : tokens) {
                System.out.println(token);
            }
            Parser parser = new Parser(tokens);
            Node astRoot = parser.parse();
            // Print the AST using the ToString method
            System.out.println("Abstract Syntax Tree (AST):");
            Interpreter interpreter = new Interpreter();
            System.out.println("Statements:");
            if (astRoot instanceof StatementsNode) {
                StatementsNode statementsNode = (StatementsNode) astRoot;
                for (Node statement : statementsNode.getStatements()) {
                    System.out.println(" - " + statement);  //  Prints statements only ONCE
                }
            } else {
                System.out.println(" - " + astRoot);  //  If it's a single statement, print it once
            }
            binaryTree(astRoot);
        } catch (Exception e) {
            // Print a detailed error message, including the exception information
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for further debugging
            // Exit the program with an error code
            System.exit(1);
        }
    }

    static void binaryTree(Node node) {
        if (node == null) {
            return;
        }
        // Check if the current node is a MathOpNode
        if (node instanceof MathOpNode) {
            MathOpNode mathOpNode = (MathOpNode) node;

            // First print data of node
            System.out.print(mathOpNode.toString() + " ");

            // Recur on left and right subtrees
            binaryTree(mathOpNode.leftOperand);
            binaryTree(mathOpNode.rightOperand);

        } else if (node instanceof IntegerNode) {
            // If the current node is an IntegerNode, print its value
            IntegerNode integerNode = (IntegerNode) node;
            System.out.print(integerNode + " ");
        }

    }

}