package frontend.nodes;

import frontend.token.TokenType;

import java.util.ArrayList;

public class MulExp {
    public ArrayList<UnaryExp> unaryExps;
    public ArrayList<TokenType> ops;
    public int dim;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<TokenType> ops) {
        this.unaryExps = unaryExps;
        this.ops = ops;
        this.dim = unaryExps.get(0).dim;
    }
}
