public class NextNode extends StatementNode{

    private String value;

    public NextNode(StringNode value){
        this.value = String.valueOf(value);
    }

    @Override
    public String toString() {
        return "NextNode: " + value;
    }
}
