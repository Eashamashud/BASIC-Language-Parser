public class BooleanExpressionNode extends Node{


    public Token.TokenType getOperator() {
        return operator;
    }

    public void setOperator(Token.TokenType operator) {
        this.operator = operator;
    }

    private Token.TokenType operator;

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    private Node left;
    private Node right;
    public BooleanExpressionNode(Token.TokenType operator, Node left, Node right) {
    }

    @Override
    public String toString() {
        return "BooleanExpressionNode";
    }
}
