import java.util.*;

public class StatementsNode extends Node {

    private final LinkedList<StatementNode> statements;

    public StatementsNode(LinkedList<StatementNode> statements){
        this.statements = statements;
    }

    public StatementsNode() {
        this.statements = new LinkedList<StatementNode>();
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    public void addstatement(StatementNode statement){
        statements.add((StatementNode)statement);
        //statements.add(statement);
    }

    public String toString(){
        return "Statements: " + statements.toString();
    }

    public void addStatement(LabeledStatementNode labeledStatementNode) {
        statements.add((LabeledStatementNode)labeledStatementNode);
    }
}



