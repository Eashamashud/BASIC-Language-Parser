import java.util.LinkedList;

import java.util.List;
import java.util.Optional;

public class Parser {

    private int currentPosition = 0; // Tracks the current token index
    private LinkedList<Token> tokens;

    public boolean moreTokens() {
        return currentPosition < tokens.size();
    }

    private TokenManager tokenManager;

    public Parser(LinkedList<Token> tokens) {
        this.tokenManager = new TokenManager(tokens);
    }

    boolean AcceptSeperators(){
        if(tokenManager.Peek(0).map(token -> token.getType() == Token.TokenType.ENDOFLINE).orElse(false)){
            return true;
        }else{
            return false;
        }
    }

    public Node parse() {
        return statements();
    }

    private Node expression() {
        Node left = term();  // ✅ Parse the LEFT operand first

        while (tokenManager.Peek(0).map(token -> token.getType() == Token.TokenType.PLUS ||
                token.getType() == Token.TokenType.MINUS).orElse(false)) {
            Token operator = tokenManager.matchAndRemove(tokenManager.Peek(0).get().getType()).get();

            Node right = term();  // ✅ Parse the RIGHT operand next

            MathOpNode.mathOp operationType;
            if (operator.getType() == Token.TokenType.PLUS) {
                operationType = MathOpNode.mathOp.ADD;
            } else {
                operationType = MathOpNode.mathOp.SUBTRACT;
            }

            left = new MathOpNode(operationType, left, right); // ✅ Ensures correct operand order
        }
        return left;
    }


    private Node term() {
        Node left = factor();

        if (tokenManager.Peek(0).map(token -> token.getType() == Token.TokenType.MULTIPLY).orElse(false)) {
            tokenManager.matchAndRemove(Token.TokenType.MULTIPLY);
            Node right = factor();
            MathOpNode.mathOp operationType = MathOpNode.mathOp.MULTIPLY;  // Ensure correct assignment
            left = new MathOpNode(operationType, left, right);
        } else if (tokenManager.Peek(0).map(token -> token.getType() == Token.TokenType.DIVIDE).orElse(false)) {
            tokenManager.matchAndRemove(Token.TokenType.DIVIDE);
            Node right = factor();
            MathOpNode.mathOp operationType = MathOpNode.mathOp.DIVIDE;  // Ensure correct assignment
            left = new MathOpNode(operationType, left, right);
        }

        return left;
    }


    private Node factor() {
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

        // To accept a WORD(variable)
        if (currentTokenType == Token.TokenType.WORD) {
            String variableName = tokenManager.Peek(0).map(Token::getValue).orElse(null);
            tokenManager.matchAndRemove(Token.TokenType.WORD);
            return new VariableNode(variableName);
        } else if(currentTokenType == Token.TokenType.STRINGLITERAL) {
            String stringName = tokenManager.Peek(0).map(Token::getValue).orElse(null);
            tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL);
            return new StringNode(stringName);
        } else if(currentTokenType == Token.TokenType.FUNCTION) {
            //String stringName = tokenManager.Peek(0).map(Token::getValue).orElse(null);
            //tokenManager.matchAndRemove(Token.TokenType.FUNCTION);
            return functionInvocation();
        }
        if (currentTokenType == Token.TokenType.MINUS) {
            // Unary minus case
            tokenManager.matchAndRemove(Token.TokenType.MINUS);
            Node operand = factor();
            return new MathOpNode(MathOpNode.mathOp.SUBTRACT, new IntegerNode(0), operand);
        } else if (currentTokenType == Token.TokenType.LPAREN) {
            // Parentheses case
            tokenManager.matchAndRemove(Token.TokenType.LPAREN);
            Node expressionNode = expression();
            tokenManager.matchAndRemove(Token.TokenType.RPAREN);
            return expressionNode;
        } else {
            // Number case
            Optional<Token> numToken = tokenManager.matchAndRemove(Token.TokenType.NUMBER);
            Optional<Token> floatToken = tokenManager.matchAndRemove(Token.TokenType.FLOAT);
            if (numToken.isPresent() || floatToken.isPresent()) {
                // Determine the appropriate node type based on the presence of a dot in the number
                String numValue = (numToken.isPresent()) ? numToken.get().getValue() : floatToken.get().getValue();
                if (numValue.contains(".")) {
                    return new FloatNode(Float.parseFloat(numValue));
                } else {
                    return new IntegerNode(Integer.parseInt(numValue));
                }
            } else {
                throw new IllegalArgumentException("Invalid factor: " + tokenManager.Peek(0));
            }
        }
    }

    // Parse a statement
    private Node statement() {

        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

        if(currentTokenType == Token.TokenType.IF){
            return ifStatement();
        } else if(currentTokenType == Token.TokenType.LABEL){
            return labelStatement();
        }  else if(currentTokenType == Token.TokenType.FUNCTION){
            return functionInvocation();
        } else if (currentTokenType == Token.TokenType.PRINT) {
            return PrintStatement();
        } else if (currentTokenType == Token.TokenType.WORD) {
            return assignment();
        } else if (currentTokenType == Token.TokenType.READ) {
            return read();
        } else if (currentTokenType == Token.TokenType.DATA) {
            return data();
        } else if (currentTokenType == Token.TokenType.INPUT) {
            return input();
        }  else if(currentTokenType == Token.TokenType.GOSUB){
            return gosub();
        } else if(currentTokenType == Token.TokenType.RETURN){
            return returnStatement();
        } else if(currentTokenType == Token.TokenType.FOR){
            return forStatement();
        } else if(currentTokenType == Token.TokenType.END){
            return end();
        } else if(currentTokenType == Token.TokenType.NEXT){
            return nextStatement();
        }else if(currentTokenType == Token.TokenType.WHILE){
            return whileStatement();
        }
        else {
            return null;
        }
    }

    // Parse a series of statements
    private Node statements() {
        // Statements should call statement until statement fails
        StatementsNode statementsNode = new StatementsNode();

        StatementNode statementNode;

        while ((statementNode = (StatementNode) statement()) != null) {
            while(AcceptSeperators()) {
                tokenManager.matchAndRemove(Token.TokenType.ENDOFLINE);
            }
            statementsNode.addstatement(statementNode);
        }
        return statementsNode;
    }

    private Node PrintStatement() {

        // Accepts a print statement and created a PrintNode
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (currentTokenType == Token.TokenType.PRINT) {
            tokenManager.matchAndRemove(Token.TokenType.PRINT);
            LinkedList<Node> printList = printList();
            if (printList != null) {
                return new PrintNode(printList);
            }
        }
        return null;
    }
    //
    private Node read() {
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (currentTokenType == Token.TokenType.READ) {
            tokenManager.matchAndRemove(Token.TokenType.READ);
            LinkedList<Node> readList = readList();
            if (readList != null) {
                return new ReadNode(readList);
            }
        }
        return null;
    }


    private LinkedList<Node> readList() {
        LinkedList<Node> expressions = new LinkedList<>();
        Node expression = expression();
        while (expression != null) {
            expressions.add(expression);
            if (tokenManager.matchAndRemove(Token.TokenType.COMMA).isPresent()) {
                expression = expression();
            } else {
                break;
            }
        }
        return expressions;
    }


    private LinkedList<Node> dataList() {
        LinkedList<Node> expressions = new LinkedList<>();
        Node expression = expression();
        while (expression != null) {
            expressions.add(expression);
            if (tokenManager.matchAndRemove(Token.TokenType.COMMA).isPresent()) {
                expression = expression();
            } else {
                break;
            }
        }
        return expressions;
    }

    private LinkedList<Node> inputList() {
        LinkedList<Node> expressions = new LinkedList<>();
        Node expression = expression();
        while (expression != null) {
            expressions.add(expression);
            if (tokenManager.matchAndRemove(Token.TokenType.COMMA).isPresent()) {
                expression = expression();
            } else {
                break;
            }
        }
        return expressions;
    }

    private LinkedList<Node> printList() {
        LinkedList<Node> expressions = new LinkedList<>();
        Node expression = expression();
        while (expression != null) {
            expressions.add(expression);
            if (tokenManager.matchAndRemove(Token.TokenType.COMMA).isPresent()) {
                expression = expression();
            } else {
                //break;
                return expressions;
            }
        }
        return expressions;
    }

    // Assignment Parser method
    private Node assignment() {

        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        Optional<Token> variableToken = tokenManager.matchAndRemove(Token.TokenType.WORD);
        if (variableToken.isPresent()) {

            VariableNode variableNode = new VariableNode(variableToken.get().getValue());
            if (tokenManager.matchAndRemove(Token.TokenType.EQUALS).isPresent()) {
                Node assignedValue = expression();
                if (assignedValue != null) {
                    return new AssignmentNode(variableNode, assignedValue);
                }
            }
        }
        return null;
    }

    private Node input() {
        // Get the current token type
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

        // Check if the current token type is INPUT
        if (currentTokenType == Token.TokenType.INPUT) {
            // Remove and match the INPUT token from the token stream
            Optional<Token> inputToken = tokenManager.matchAndRemove(Token.TokenType.INPUT);

            // Check if an INPUT token was found
            if (inputToken.isPresent()) {
                // Remove and match the STRINGLITERAL token from the token stream
                Optional<Token> variableToken = tokenManager.matchAndRemove(Token.TokenType.STRINGLITERAL);

                // Check if a STRINGLITERAL token was found
                if (variableToken.isPresent()) {
                    // Create a StringNode for the string literal
                    StringNode stringNode = new StringNode(variableToken.get().getValue());

                    // Initialize a list to store the input variables
                    LinkedList<Node> input = new LinkedList<>();

                    // Loop to parse the comma-separated variables after the string literal
                    while (tokenManager.matchAndRemove(Token.TokenType.COMMA).isPresent()) {
                        // Get the token type after the comma
                        Token.TokenType inputTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

                        // Check the token type
                        if (inputTokenType == Token.TokenType.WORD) {
                            // Remove and match the WORD token representing a variable name
                            Optional<Token> optional = tokenManager.matchAndRemove(Token.TokenType.WORD);
                            Token variable = optional.get();

                            // Create a VariableNode for the variable
                            VariableNode variableNode = new VariableNode(variable.getValue());

                            // Add the VariableNode to the list of input variables
                            input.add(variableNode);
                        }
                    }
                    // Check if the list of input variables is empty
                    if (input.isEmpty()) {
                        // Throw a runtime exception if no variables are provided after the string literal
                        throw new RuntimeException("INPUT statement expects variables after the string literal.");
                    } else {
                        // Return a new InputNode with the string literal and input variables
                        return new InputNode(stringNode, input);
                    }
                }
            }
        }
        // Return null if the current token type is not INPUT or if INPUT token was not found
        return null;
    }


private Node data() {
    LinkedList<Node> dataList = new LinkedList<>();

    // Get the current token type
    Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

    // Check if the current token type is DATA
    if (currentTokenType == Token.TokenType.DATA) {
        // Remove and match the DATA token from the token stream
        Optional<Token> dataToken = tokenManager.matchAndRemove(Token.TokenType.DATA);

        // Check if a DATA token was found
        if (dataToken.isPresent()) {
            // Loop to parse the comma-separated values
            while (true) {
                // Get the token type
                Token.TokenType inputTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

                // Break the loop if no more tokens are found
                if (inputTokenType == null || inputTokenType == Token.TokenType.ENDOFLINE) {
                    break;
                }
                // Remove and match the next token
                Optional<Token> optionalToken = tokenManager.matchAndRemove(inputTokenType);
                Token token = optionalToken.get();

                // Convert token value to appropriate type and add to dataList
                switch (token.getType()) {
                    case STRINGLITERAL:
                        dataList.add(new StringNode(token.getValue())); // Token gets the value
                        break;
                    case NUMBER:
                        dataList.add(new IntegerNode(Integer.parseInt(token.getValue())));
                        break;
                    case FLOAT:
                        dataList.add(new FloatNode(Float.parseFloat(token.getValue())));
                        break;
                    default:
                        throw new RuntimeException("Unexpected token type: " + token.getType());
                }

                // If next token is a comma, consume it
                if (tokenManager.Peek(0).map(t -> t.getType() == Token.TokenType.COMMA).orElse(false)) {
                    tokenManager.matchAndRemove(Token.TokenType.COMMA);
                }
            }
        }
    }
    // Create a DataNode containing the list of parsed data nodes
    return new DataNode(dataList);
}


    private Node labelStatement(){
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (currentTokenType == Token.TokenType.LABEL){
            Optional<Token> end = tokenManager.matchAndRemove(Token.TokenType.LABEL);
            return new LabeledStatementNode();
        }else{
            throw new RuntimeException();
        }
    }


    private Node gosub() {
        var gsb = tokenManager.matchAndRemove(Token.TokenType.GOSUB);
        if (gsb.isEmpty())
            return null;
        // Parse the identifier following GOSUb
        Optional<Token> identifier = tokenManager.matchAndRemove(Token.TokenType.WORD);
        if (identifier.isPresent()) {
            String identifierLabel = identifier.get().getValue();
            return new GOSUBNode(identifierLabel);
        } else {
            throw new RuntimeException();// throw exception
        }
    }

    private Node returnStatement(){
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (currentTokenType == Token.TokenType.RETURN){
            Optional<Token> end = tokenManager.matchAndRemove(Token.TokenType.RETURN);
            return new ReturnNode();
        }else{
            throw new RuntimeException();
        }
    }

    private Node forStatement() {
        tokenManager.matchAndRemove(Token.TokenType.FOR); // Consume the FOR token
        Optional<Token> variableTokenOptional = tokenManager.matchAndRemove(Token.TokenType.WORD); // Consume the loop variable
        Token variableToken = variableTokenOptional.orElseThrow(); // Extract the token
        tokenManager.matchAndRemove(Token.TokenType.EQUAL); // Consume the EQUAL token
        Node startValue = expression(); // Parse the start value
        tokenManager.matchAndRemove(Token.TokenType.TO); // Consume the TO token
        Node endValue = expression(); // Parse the end value

        Node stepValue = null;
        if (tokenManager.Peek(0).map(Token::getType).orElse(null) == Token.TokenType.STEP) {
            tokenManager.matchAndRemove(Token.TokenType.STEP); // Consume the STEP token
            stepValue = expression(); // Parse the step value
        }

        // Construct and return a ForStatementNode
        return new ForNode(variableToken.getValue(), startValue, endValue, stepValue);
    }

   private Node nextStatement() {
       // Get the current token type
       Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);

       // Check if the current token type is NEXT
       if (currentTokenType == Token.TokenType.NEXT) {
           // Remove and match the NEXT token from the token stream
           Optional<Token> nextToken = tokenManager.matchAndRemove(Token.TokenType.NEXT);

           // Check if a DATA token was found
           if (nextToken.isPresent()) {
               // Remove and match the WORD token from the token stream
               Optional<Token> wordToken = tokenManager.matchAndRemove(Token.TokenType.WORD);

               // Check if a WORDLITERAL token was found
               if (wordToken.isPresent()) {
                   // Create a StringNode for the string literal

                   StringNode stringNode = new StringNode(wordToken.get().getValue());
                   return new NextNode(stringNode);
               }
           }
       }return null;
   }

    private Node functionInvocation(){
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if(currentTokenType == Token.TokenType.FUNCTION){
            Optional<Token> functionToken = tokenManager.matchAndRemove(Token.TokenType.FUNCTION);
            if(functionToken.isPresent()){
                Optional<Token> lParenToken = tokenManager.matchAndRemove(Token.TokenType.LPAREN);
                List<Node> parameters = parseParameterList();
                tokenManager.matchAndRemove(Token.TokenType.RPAREN);
                return new FunctionInvocationNode(functionToken, parameters);
            }else{
                throw new RuntimeException("Expected FUNCTION token");
            }
        }return null;
    }

    private List<Node> parseParameterList(){
        List<Node> parameters = new LinkedList<>();
        while(tokenManager.Peek(0).map(Token::getType).orElse(null) != Token.TokenType.RPAREN) {
            Node parameter = expression();
            parameters.add(parameter);

            if(tokenManager.Peek(0).map(Token::getType).orElse(null) == Token.TokenType.COMMA){
                tokenManager.matchAndRemove(Token.TokenType.COMMA);
            }else if(tokenManager.Peek(0).map(Token::getType).orElse(null) == Token.TokenType.RPAREN){
                //throw new RuntimeException("Expected ',' or ')'");
                return parameters;
            }
        }return parameters;
    }

    // Method to parse the END statement
    private Node end(){
        Token.TokenType currentTokenType = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (currentTokenType == Token.TokenType.END){
            Optional<Token> end = tokenManager.matchAndRemove(Token.TokenType.END);
            return new EndNode();
        }else{
            return null;
        }
    }

    private Node booleanExpression() {
        Node left = expression();
        Token.TokenType operator = tokenManager.Peek(0).map(Token::getType).orElse(null);
        if (operator == Token.TokenType.GREATER_THAN || operator == Token.TokenType.GREATERTHAN_EQUALTO ||
                operator == Token.TokenType.LESS_THAN || operator == Token.TokenType.LESSTHAN_EQUALTO ||
                operator == Token.TokenType.NOTEQUALS || operator == Token.TokenType.EQUAL) {
            tokenManager.matchAndRemove(operator); // Consume the operator token
            Node right = expression();
            // Create a BooleanExpressionNode with left, right, and operator
            return new BooleanExpressionNode(operator, left, right);
        } else {
            // Handle error: Expected comparison operator
            return null;
        }
    }

    private Node ifStatement() {
        tokenManager.matchAndRemove(Token.TokenType.IF); // Consume the IF token
        Node condition = booleanExpression();  // Parse the condition

        tokenManager.matchAndRemove(Token.TokenType.THEN); // Consume THEN
        Node trueBranch = statement(); // Parse the THEN branch

        Node falseBranch = null;
        if (tokenManager.Peek(0).map(Token::getType).orElse(null) == Token.TokenType.ELSE) {
            tokenManager.matchAndRemove(Token.TokenType.ELSE);
            falseBranch = statement();  //  Parse the ELSE branch
        }

        return new IfStatementNode(condition, trueBranch, falseBranch);  //  Add to AST
    }


    private Node whileStatement() {
        tokenManager.matchAndRemove(Token.TokenType.WHILE); // Consume the WHILE token

        // Parse the conditional expression
        Node condition = booleanExpression();

        // Parse the block of code
        Node body = statement();

        if (tokenManager.Peek(0).map(Token::getType).orElse(null) == Token.TokenType.LABEL) {
            tokenManager.matchAndRemove(Token.TokenType.LABEL); // Consume the ELSE token

            // Parse the block of code for the false branch
            Node falseBranch = statement();

            // Construct and return an WhileNode with both branches
            return new WhileNode(condition,  falseBranch);
        } else {
            // Construct and return an WhileNode with only the true branch
            return new WhileNode(condition,  null);
        }
    }

}