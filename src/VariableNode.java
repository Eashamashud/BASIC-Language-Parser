public class VariableNode extends Node {

    private final String variableName;

    //Constructor
    public VariableNode(String variableName){
        this.variableName = variableName;
    }

    // Accessor
    public String getVariableName(){
        return variableName;
    }

    @Override
    public String toString(){
        return "VariableName: " + variableName.toString();
    }

}
