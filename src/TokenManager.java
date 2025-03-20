import java.util.LinkedList;
import java.util.Optional;

public class TokenManager {

    private LinkedList<Token>tokenStream;

    public TokenManager(LinkedList<Token>tokenStream){
        this.tokenStream = tokenStream;
    }

    // Peek “j” tokens ahead and return the token if we aren’t past the end of the token list
    public Optional<Token> Peek(int j){
        if(j >= 0 && j<tokenStream.size()) {
            return Optional.of(tokenStream.get(j));

        }else {
            return Optional.empty();
        }
    }

    public boolean MoreTokens() {
        if (!tokenStream.isEmpty()) {
            return true;
        }else {
            return false;
        }
    }

    public Optional<Token> matchAndRemove(Token.TokenType type){
        if(!tokenStream.isEmpty() && tokenStream.getFirst().getType() == type) {
            return Optional.of(tokenStream.removeFirst());

        }else {
            return Optional.empty();
        }
    }

}
