package frontend.nodes;

import frontend.token.Token;
import frontend.token.TokenType;

public class PrimaryExp {
    public Exp exp;
    public LVal lVal;
    public Token number;
    public int dim;

    public PrimaryExp(Exp exp, LVal lVal, Token number) {
        assert number.type == TokenType.INTCON;
        this.exp = exp;
        this.lVal = lVal;
        this.number = number;
        if (exp != null) { this.dim = ((AddExp)exp).dim; }
        else if (lVal != null) { this.dim = lVal.dim; }
        else { this.dim = 0; }
    }
}
