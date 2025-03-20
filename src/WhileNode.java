public class WhileNode extends StatementNode{
    private Node conditon;
    private Node body;

    public WhileNode(Node condition, Node body) {
        this.conditon = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "WhileNode: " + conditon ;
    }
}
