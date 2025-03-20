import java.util.LinkedList;

public class InputNode extends StatementNode{

    private final LinkedList<Node> input;
    private final StringNode stringNode;


    public InputNode(StringNode stringNode,  LinkedList<Node> input ){
        this.stringNode = stringNode;
        this.input = input;
    }

    public LinkedList<Node> getInput() {
        return input;
    }

    public StringNode getStringNode(){
        return stringNode;
    }


    @Override
    public String toString() {
        return "InputNode: " +  stringNode.toString() + " " + input.toString();
    }


    public void add(Node expression) {
        input.add(expression);
    }

}
