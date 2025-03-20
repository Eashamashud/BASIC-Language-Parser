public class IfStatementNode extends StatementNode {

    private Node condition;
    private Node trueStatements;
    private Node falseStatements;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public IfStatementNode(Node condition, Node trueStatements, Node falseStatements) {
        this.condition = condition;
        this.trueStatements = trueStatements;
        this.falseStatements = falseStatements;
    }

    public IfStatementNode(Node condition, Node trueStatements) {
        this.condition = condition;
        this.trueStatements = trueStatements;
        this.falseStatements = null;
    }

    public IfStatementNode(Node condition, Node trueStatements, String label) {
        this.condition = condition;
        this.trueStatements = trueStatements;
        this.falseStatements = null;
        this.label = label;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getFalseStatements() {
        return falseStatements;
    }

    public Node getTrueStatements() {
        return trueStatements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IfStatementNode(Condition: ").append(condition.toString());

        if (trueStatements != null) {
            sb.append(", Then: ").append(trueStatements.toString());
        }

        if (falseStatements != null) {
            sb.append(", Else: ").append(falseStatements.toString());
        }

        sb.append(")");
        return sb.toString();
    }


}
