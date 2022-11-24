package frontend.nodes;

import frontend.symbol.VarSym;
import frontend.token.Token;

public class FuncFParam {
    public int dim;
    public Token ident;
    public ConstExp constExp;
    public VarSym varSym;

    public FuncFParam(int dim, Token ident, ConstExp constExp,VarSym varSym) {
        this.dim = dim;
        this.ident = ident;
        this.constExp = constExp;
        this.varSym = varSym;
    }
}
