public class AssignmentNode extends StatementNode {

    private final VariableNode variableNode;
    private final Node assignedValue;

    public AssignmentNode(VariableNode variableNode , Node assignedValue){
        this.variableNode = variableNode;
        this.assignedValue = assignedValue;
    }

    public VariableNode getVariableNode(){
        return variableNode;
    }

    public Node getAssignedValue() {
        return assignedValue;
    }

    @Override
    public String toString(){

        return variableNode + "=" + "AssignmentNode " + assignedValue;
    }


}

