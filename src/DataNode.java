import java.util.LinkedList;

public class DataNode extends StatementNode {

    private final LinkedList<Node> data;

    public DataNode( LinkedList<Node> data) {

        this.data = data;
    }

    public LinkedList<Node> getData() {
        return data;
    }


    @Override
    public String toString() {
        return "DataNode"  +  " " + data.toString();
    }

    public void add(Node expression){
        data.add(expression);

    }
}