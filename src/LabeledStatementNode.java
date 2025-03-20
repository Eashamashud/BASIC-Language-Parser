
public class LabeledStatementNode extends StatementNode{

    private String label;
    private StatementNode statementNode;

    @Override
    public String toString() {
        return "LabeledStatementNode:";
    }

    public String getLabel() {
        return label;
    }

    public StatementNode getStatementNode() {
        return statementNode;
    }

    public LabeledStatementNode(String label, StatementNode statementNode){
        this.label = label;
        this.statementNode = statementNode;
    }

    public LabeledStatementNode(){
    }
}
