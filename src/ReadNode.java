import java.util.LinkedList;

class ReadNode extends StatementNode{

    private final LinkedList<Node> read;

    ReadNode(LinkedList<Node> read) {
        this.read = read;
    }

    public LinkedList<Node> getRead() {
        return read;
    }

    public void add(Node expression) {
        read.add(expression);
    }
    @Override
    public String toString() {
        return "ReadNode" + read.toString();
    }
}
