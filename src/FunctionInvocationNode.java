import java.util.List;
import java.util.Optional;


public class FunctionInvocationNode extends Node{
    public Optional<Token> getFunctionToken() {
        return functionToken;
    }

    public void setFunctionToken(Optional<Token> functionToken) {
        this.functionToken = functionToken;
    }

    public Optional<Token> functionToken;

    public List<Node> getParameters() {
        return parameters;
    }

    public void setParameters(List<Node> parameters) {
        this.parameters = parameters;
    }

    public List<Node> parameters;

    public FunctionInvocationNode(Optional<Token> functionToken, List<Node> parameters) {
        this.functionToken = functionToken;
        this.parameters = parameters;
        //super();
    }

    @Override
    public String toString() {
        return "FunctionNode: " + functionToken + " " + parameters;
    }
}
