package frontend.nodes;

import frontend.token.Token;
import frontend.token.TokenType;

public class FuncDef {
    public TokenType funcType;
    public Token ident;
    public FuncFParams funcFParams;
    public Block block;

    public FuncDef(TokenType funcType, Token ident, FuncFParams funcFParams, Block block) {
        assert funcType.equals(TokenType.VOIDTK)|| funcType.equals(TokenType.INTTK);
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }
}
