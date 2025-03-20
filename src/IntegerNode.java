
public class IntegerNode extends Node {

    private final int number;

    // Constructor
    public IntegerNode(int number) {
        this.number = number;
    }

    // Accessor
    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return  "IntegerNode" + "(" + Integer.toString(number) + ")" ;
    }

}
