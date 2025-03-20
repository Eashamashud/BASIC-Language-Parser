import java.util.LinkedList;
import java.util.List;

class PrintNode extends StatementNode {


    private final LinkedList<Node> printList;

    // Constructor that takes a list of Node objects, where each Node represents an expression to be printed
    public PrintNode(LinkedList<Node> printList){
        this.printList = printList;
    }

    public PrintNode(){
        printList = new LinkedList<>();
    }

    // Getter accesses the list of expressions
    public LinkedList<Node> getPrintList() {
        return printList;
    }

    @Override
    public String toString() {
        return "PrintNode" + printList.toString();
    }

    public void add(Node expression) {
        printList.add(expression);
    }
}
