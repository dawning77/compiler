package frontend.nodes;

import frontend.token.TokenType;

import java.util.ArrayList;

public class EqExp {
    public ArrayList<RelExp> relExps;
    public ArrayList<TokenType>ops;

    public EqExp(ArrayList<RelExp> relExps, ArrayList<TokenType> ops) {
        this.relExps = relExps;
        this.ops = ops;
    }
}
