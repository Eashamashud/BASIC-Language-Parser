public class StringNode extends Node {

    private final String stringLiteral;

    public StringNode(String stringLiteral) {
        this.stringLiteral = stringLiteral;
    }

    public String getStringLiteral() {
        return stringLiteral;
    }

    @Override
    public String toString() {
        return
                "StringLiteral: " + stringLiteral.toString();
    }
}
