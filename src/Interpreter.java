import java.util.*;
import java.util.Queue;

public class Interpreter {

    // Hashmaps for variable storage
    private static HashMap<String,Integer> integerVariables;
    private static HashMap<String,Float> floatVariables;
    private static HashMap<String,String> stringVariables;
    public List<String>Prints;
    public List<String>Inputs;
    boolean loop = true;
    StatementNode currentStatement;
    Stack <StatementNode>stack = new Stack<>();

    private HashMap<String, LabeledStatementNode> stringLabeledStatementNodeHashmap;
    private static Queue<String> dataQueue;
    private TokenManager tokenManager;

    boolean testmode = true;

    // Constructors
    public Interpreter(){

        integerVariables = new HashMap<>();
        floatVariables = new HashMap<>();
        stringVariables = new HashMap<>();
        stringLabeledStatementNodeHashmap = new HashMap<>();
        dataQueue = new LinkedList<>();
    }

    // Method to traverse the StatementsNode and set the 'next' memeber of each 'StatementNode' to the next statement in the sequence
    private void buildStatementList(StatementsNode statements){
        StatementNode current = null;
        for(StatementNode statement : statements.getStatements()){
            if(current != null){
                current.setNext(statement);
            }
            current = statement;
        }currentStatement = statements.getStatements().get(0);
    }

    public void Interpret(StatementNode statement) {
        while(loop && currentStatement!=null){
        // Check the type of statement and interpret accordingly
        if (statement instanceof ReadNode) {
            // If it's a ReadNode, interpret it as a read operation
            interpretReadNode((ReadNode) statement);
        } else if (statement instanceof AssignmentNode) {
            // If it's an AssignmentNode, interpret it as an assignment operation
            interpreterAssignmentNode((AssignmentNode) statement);
        } else if (statement instanceof InputNode) {
            // If it's an InputNode, interpret it as an input operation
            interpretInputNode((InputNode) statement);
        } else if (statement instanceof PrintNode) {
            // If it's a PrintNode, interpret it as a print operation
            interpretPrintNode((PrintNode) statement);
        }  else if(statement instanceof IfStatementNode){
            interpretIfStatementNode((IfStatementNode) statement);
        } else if(statement instanceof GOSUBNode){
            interpretGosubNode((GOSUBNode) statement);
            GOSUBNode gosubNode = (GOSUBNode) statement;
            // Push the gosub's next statement onto the stack
            stack.push(currentStatement.getNext());
            // Set the next statement to the label associated with the GosubNode
            currentStatement = stringLabeledStatementNodeHashmap.get(gosubNode.getLabel());
        } else if(statement instanceof ReturnNode){
            // Pop a value from the stack
            StatementNode returnNode = stack.pop();
            // Set the next to be the return node
            currentStatement = returnNode;
        } else if(statement instanceof ForNode){
            ForNode forNode =(ForNode) statement;
            // Get or create the variable from the variable map
            int variableValue = integerVariables.getOrDefault(forNode.value, 0);
            // Initialize the variable if its a new variable
            if(!integerVariables.containsKey(forNode.value)){
                integerVariables.put(forNode.value, forNode.startValue);
            }
            // Update the variable by the step value
            variableValue += forNode.stepValue;
            integerVariables.put(forNode.value, variableValue);

            // Check if the variable exceeds the limit value
            if((forNode.stepValue > 0 && variableValue > forNode.endValue) ||
               (forNode.stepValue < 0 && variableValue < forNode.endValue) ){
                // Skip over instructions until we find the matching NEXT
                int depth = 1;
                while(depth > 0){
                    currentStatement = currentStatement.getNext();
                    if(currentStatement instanceof ForNode){
                        depth++;
                    } else if(currentStatement instanceof NextNode){
                        depth--;
                    }
                }
            } else{
                // Push the FOR statement on the stack
                stack.push(currentStatement);
            }
        }else if(statement instanceof NextNode){
            // Pop from the stack;
            StatementNode nextNode = stack.pop();
           // Set next to be the corresponding StatementNode
            currentStatement = nextNode;
        }else if(statement instanceof EndNode){
            loop = false;
        }
    }}

    // Accessors
    public HashMap<String, Integer> getIntegerVariables(){
        return integerVariables;
    }

    public HashMap<String, Float> getFloatVariables(){
        return floatVariables;
    }

    public HashMap<String, String> getStringVariables(){
        return stringVariables;
    }

    public HashMap<String, LabeledStatementNode> getStringLabeledStatementNodeHashmap(){
        return stringLabeledStatementNodeHashmap;
    }

    public static Queue<String> getDataQueue(String intVar, IntegerNode integerNode){
        return dataQueue;
    }

    public static Queue<String> getDataQueue(String intVar, FloatNode floatNode){
        return dataQueue;
    }

    public static Queue<String> getDataQueue(String intVar, StringNode stringNode){
        return dataQueue;
    }

    public static Queue<String> getDataQueue(){
        return dataQueue;
    }

    // Method to process data and insert their contents into dataQueue
    private void processData(StatementsNode statements) {
            // Traverse the AST to find DATA statements
            // Iterates over each StatementNode within the statements object
            for (StatementNode statement : statements.getStatements()) {
                // Check if the Node is a DATA node
                if (statement instanceof DataNode) {
                    DataNode dataNode = (DataNode) statement;
                    // Create a linked list to store all the data values
                    LinkedList<Node> dataValues = dataNode.getData();
                    //for (Object dataValue : dataValues)
                    for (Node dataValue : dataValues) {
                        dataQueue.add(String.valueOf(dataValue));
                        System.out.println(dataValue);
                    }
                }
            }
     }


        // Method to process labels and popoulate the labels hashmap
        private void labeledStatementNode(StatementsNode statements ){
            // Traverse the AST to find labeled statements
            // Iterate through each statement in the AST
            for(StatementNode statement : statements.getStatements()){
                if(statement instanceof LabeledStatementNode){
                    LabeledStatementNode labeledStatementNode = (LabeledStatementNode) statement;
                    // Store labeled statement in the hashmap
                    String label = labeledStatementNode.getLabel();
                    stringLabeledStatementNodeHashmap.put(label, labeledStatementNode);
                }
            }

        }

        // Method to handle read statements by popping values from the dataQueue and storing them into the appropriate variable map.
        private void readDataValues(String variableName, Node dataTypeNode) {
        // Checks if dataQueue is empty
        if (!dataQueue.isEmpty()) {
            // Removes and returns the head of the dataQueue
            // The value removed from the queue is assigned to the value variable
            String value = dataQueue.poll();
            // Checking the type of dataTypeNode
            if (dataTypeNode instanceof IntegerNode) {
                // Parse the string value to an Integer
                integerVariables.put(variableName, Integer.parseInt(value));
            } else if (dataTypeNode instanceof FloatNode) {
                // Parse the string value to a Float
                floatVariables.put(variableName, Float.parseFloat(value));
            } else if (dataTypeNode instanceof StringNode) {
                stringVariables.put(variableName, value);
            } else {
                // Handle other types or default case here
                System.out.println("Error: Unsupported data type.");
            }
        } else {
            System.out.println("Error: No more data available for READ.");
        }
    }

    private void interpretReadNode(ReadNode node) {
        for(int i = 0; i < node.getRead().size(); i++){
            VariableNode variableNode = (VariableNode) node.getRead().get(i);
            if (variableNode.getVariableName().contains("%")){
                    // Make sure the interpreterdatatype is  float and add to our hashmap Of float
                    float floatValue = Float.parseFloat(dataQueue.poll());
                    floatVariables.put(variableNode.getVariableName(), floatValue);
            }else if(variableNode.getVariableName().contains("$")){
                String stringValue = dataQueue.poll();
                stringVariables.put(variableNode.getVariableName(), stringValue);
            }else{
                int intValue = Integer.parseInt(dataQueue.poll());
                integerVariables.put(variableNode.getVariableName(), intValue);
            }

        }throw new RuntimeException("No matching data available for READ.");
    }


     private void interpreterAssignmentNode(AssignmentNode node) {
    }

    private void interpretPrintNode(PrintNode node){
        if(testmode) {
            for(int i = 0; i < node.getPrintList().size(); i++){
                System.out.println(node);
            }
        }
    }

    private void interpretIfStatementNode(IfStatementNode statement) {
        IfStatementNode ifNode = (IfStatementNode) statement;
        // Evaluate the boolean expression
        BooleanExpressionNode condition = (BooleanExpressionNode) ifNode.getCondition();
        boolean result = false;
        // If true, look up the label in the label hashmap and set currentStatement to be that label.
        switch (condition.getOperator()) {
            case Token.TokenType.EQUALS:
                result = evaluate(condition.getLeft()) == evaluate(condition.getRight());
                break;
            case Token.TokenType.NOTEQUALS:
                result = evaluate(condition.getLeft()) != evaluate(condition.getRight());
                break;
            case Token.TokenType.LESS_THAN:
                result = (Float) evaluate(condition.getLeft()) < (Float) evaluate(condition.getRight());
                break;
            case Token.TokenType.GREATER_THAN:
                result = (Float) evaluate(condition.getLeft()) > (Float) evaluate(condition.getRight());
                break;
            case Token.TokenType.LESSTHAN_EQUALTO:
                result = (Float) evaluate(condition.getLeft()) <= (Float) evaluate(condition.getRight());
                break;
            case Token.TokenType.GREATERTHAN_EQUALTO:
                result = (Float) evaluate(condition.getLeft()) >= (Float) evaluate(condition.getRight());
                break;
        }
        if (result) {
            currentStatement = stringLabeledStatementNodeHashmap.get(ifNode.getLabel());
        } else {
            currentStatement = currentStatement.getNext();

        }
    }

    private void interpretGosubNode(GOSUBNode node){

    }

    private void interpretreturnNode(ReturnNode node){
    }

    private void interpretforNode(ForNode node){
    }

    private void interpretnextNode(NextNode node){
    }

    private void interpretendNode(EndNode node){

    }


    public int evaluateInt(Node node){
        if(node instanceof VariableNode){
            // Check the value of the variable in the variables map
            return integerVariables.get(((VariableNode) node).getVariableName());
        } else if(node instanceof IntegerNode){
            return ((IntegerNode)node).getNumber();
        } else if(node instanceof MathOpNode){
            MathOpNode mathOpNode =  (MathOpNode) node;
            int leftValue = evaluateInt(mathOpNode.getLeft());
            int rightValue = evaluateInt(mathOpNode.getRight());
            // Perform the operation based on the operator
            switch(mathOpNode.getOperator()) {
                case ADD:
                    return leftValue + rightValue;
                case SUBTRACT:
                    return leftValue - rightValue;
                case MULTIPLY:
                    return leftValue * rightValue;
                case DIVIDE:
                    return leftValue / rightValue;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + mathOpNode.getOperator());
            }
        } else if(node instanceof FunctionInvocationNode){
            //Check if the FunctionInvocationNode represents a built-in function call
            Optional<Token> functionInvocationNode = ((FunctionInvocationNode)node).getFunctionToken();
            // Get the value of the function token
            if(functionInvocationNode.get().getValue().equals("RANDOM")){
                return RANDOM();
            }else if(functionInvocationNode.get().getValue().equals("VAL")){
                List<Node> functionInvocationNodeparams = ((FunctionInvocationNode)node).getParameters();
                return VAL(evaluateString(functionInvocationNodeparams.get(0)));
            }

        } else{
        throw new RuntimeException("Unsupported node type: " + node.getClass());
    }
        return 0;
    }

    public float evaluateFloat(Node node){
        if(node instanceof VariableNode){
            return floatVariables.get(((VariableNode) node).getVariableName());
        }else if(node instanceof FloatNode){
            return ((FloatNode)node).getFloat();
        }else if(node instanceof MathOpNode) {
            MathOpNode mathOpNode = (MathOpNode) node;
            float leftValue = evaluateFloat(mathOpNode.getLeft());
            float rightValue = evaluateFloat(mathOpNode.getRight());
            switch (mathOpNode.getOperator()) {
                case ADD:
                    return leftValue + rightValue;
                case SUBTRACT:
                    return leftValue - rightValue;
                case MULTIPLY:
                    return leftValue * rightValue;
                case DIVIDE:
                    return leftValue / rightValue;
                default:
                    throw new IllegalArgumentException("Unknown operator: " + mathOpNode.getOperator());
            }
        }else if(node instanceof FunctionInvocationNode){
            //Check if the FunctionInvocationNode represents a built-in function call
            Optional<Token> functionInvocationNode = ((FunctionInvocationNode)node).getFunctionToken();
            if(functionInvocationNode.get().getValue().equals("VAL_FLOAT")){

                List<Node> functionInvocationNodeparams = ((FunctionInvocationNode)node).getParameters();
                return VAL_FLOAT(evaluateString(functionInvocationNodeparams.get(0)));
            }
        } else{
            throw new RuntimeException("Unsupported node type: " + node.getClass());
        }
        return 0;
    }

    private String evaluateString(Node node){
        if(node instanceof VariableNode){
            return stringVariables.get(((VariableNode) node).getVariableName());
        }else if(node instanceof StringNode){
            return ((StringNode)node).getStringLiteral();
        }else{
            throw new RuntimeException("Unsupported node type: " + node.getClass());
        }
    }

    public Object evaluate(Node node){
        if(node instanceof VariableNode){
         VariableNode variableNode = (VariableNode) node;
         if(variableNode.getVariableName().contains("$")){
             return evaluateInt(variableNode);
         }else if(variableNode.getVariableName().contains("%")){
             return evaluateFloat(variableNode);
         }else{
             return evaluateString(variableNode);
         }
        }
        else if(node instanceof StringNode){
            return evaluateString(node);
        }else if(node instanceof IntegerNode){
            return evaluateInt(node);
        } else if(node instanceof FloatNode){
            return evaluateFloat(node);
        } else if (node instanceof FunctionInvocationNode) {
            Optional<Token> functionInvocationNode = ((FunctionInvocationNode) node).getFunctionToken();
            if(functionInvocationNode.get().getValue().equals("NUM$")){
                evaluateString(node);
            }else if(functionInvocationNode.get().getValue().equals("MID$")){
                evaluateString(node);
            }else if(functionInvocationNode.get().getValue().equals("LEFT$")){
                evaluateString(node);
            }else if(functionInvocationNode.get().getValue().equals("RIGHT$")){
                evaluateString(node);
            }else if(functionInvocationNode.get().getValue().equals("VAL")){
                evaluateInt(node);
            }else if(functionInvocationNode.get().getValue().equals("VAL_FLOAT")){
                evaluateFloat(node);
            }else if(functionInvocationNode.get().getValue().equals("NUM$_FLOAT")){
                evaluateString(node);
            } else if(functionInvocationNode.get().getValue().equals("RANDOM")) {
                evaluateInt(node);
            }

        }
        return node;
    }


       // Built in Functions

        //Returns a random generator
        public static int RANDOM(){
            return (int)(Math.random() * Integer.MAX_VALUE);
        }

        // Method to extract the leftmost N characters from the string
        public static String LEFT$(String data, int characters){
            if(characters <= 0){
                return "";
            }
            if(characters >= data.length()){
                return data;
            }
            return data.substring(0, Math.min(characters, data.length()));
        }

        // Method to extract the rightmost N characters from the string
        public static String RIGHT$ (String data, int characters){
            if(characters<=0){
                return null;
            }
            return data.substring(Math.max(0, data.length()-characters));

        }

        //Returns the characters of the string, starting from the 2nd argument and taking the 3rd argument as the count
        public static String MID$(String data, int beginIndex, int count){
            int endIndex = Math.min(beginIndex + count, data.length());
            return data.substring(beginIndex, endIndex);
        }

        // Converts a number to a String
        public static String NUM$(int number){
            String stringValueInt = String.valueOf(number);
            return stringValueInt;

        }

        // Converts a float to a String
        public static String NUM$_FLOAT(float decimal){
            String stringValueFloat = String.valueOf(decimal);
            return stringValueFloat ;
        }

        // Converts a String to a float
        public static float VAL_FLOAT(String data){
            return Float.parseFloat(data);
        }

        // Converts a String to an Integer
        public static int VAL(String data) {
            try {
                int integerValue = Integer.parseInt(data);
                return integerValue;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public void interpretInputNode(InputNode node) {
        if (testmode) {
            return; // Skip actual input in test mode
        }
        // Get the prompt message from the StringNode, if available
        if (node.getStringNode() != null) {
            System.out.print(node.getStringNode().getStringLiteral() + " ");
        }

        // Read input from the user
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        // Get the variable node where the value will be stored
        VariableNode variable = (VariableNode) node.getInput().get(0);
        String variableName = variable.getVariableName();

        // Determine the type of variable and store input accordingly
        if (variableName.endsWith("%")) { // Float variable (e.g., A%)
            try {
                float floatValue = Float.parseFloat(userInput);
                floatVariables.put(variableName, floatValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Expected a float.");
            }
        } else if (variableName.endsWith("$")) { // String variable (e.g., NAME$)
            stringVariables.put(variableName, userInput);
        } else { // Integer variable (e.g., A)
            try {
                int intValue = Integer.parseInt(userInput);
                integerVariables.put(variableName, intValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Expected an integer.");
            }
        }
    }

}





