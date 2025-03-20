public class GOSUBNode extends StatementNode{

    private String gosub;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private String label;

    public GOSUBNode(String gosub){
       this.gosub = gosub;
   }
    
   public Object getGosub(String gosub) {
       return gosub;
   }


    @Override
    public String toString() {
        return "GOSUBnode: " + gosub;
    }
}
