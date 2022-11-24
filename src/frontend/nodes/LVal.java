package frontend.nodes;

import frontend.token.Token;

import java.util.ArrayList;

public class LVal {
    public Token ident;
    public ArrayList<Exp> exps;
    public int dim;

    public LVal(Token ident, ArrayList<Exp> exps,int dim) {
        this.ident = ident;
        this.exps = exps;
        this.dim = dim;
    }
}
