public class MathOpNode extends Node {


    public Node getLeft() {
        return leftOperand;
    }

    public Node getRight() {
        return rightOperand;
    }

    public mathOp getOperator() {
        return operation;
    }

    public enum mathOp{
        ADD, SUBTRACT, MULTIPLY, DIVIDE;
    }

    public final mathOp operation;
    public final Node leftOperand;
    public final Node rightOperand;

    public MathOpNode(mathOp operation, Node leftOperand, Node rightOperand) {
        this.operation = operation;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }
    @Override
    public String toString() {
        return String.format("MathOpNode(%s %s %s)",  operationToString() , rightOperand, leftOperand);
    }

    private String operationToString() {
        switch (operation) {
            case ADD:
                return "+";
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            default:
                throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
    }









}