package frontend.nodes;

import frontend.token.Token;
import frontend.token.TokenType;

public class UnaryExp {
    public PrimaryExp primaryExp;
    public Token ident;
    public FuncRParams funcRParams;
    public TokenType unaryOp;
    public UnaryExp unaryExp;
    public int dim;

    public UnaryExp(PrimaryExp primaryExp, Token ident, FuncRParams funcRParams,
            TokenType unaryOp, UnaryExp unaryExp,int dim) {
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.dim = dim;
    }
}
