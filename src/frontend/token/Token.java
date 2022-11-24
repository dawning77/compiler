package frontend.token;

public class Token {
    public TokenType type;
    public String val;
    public int line;

    public Token(TokenType type, String val,int line) {
        this.type = type;
        this.val = val;
        this.line = line;
    }
}
