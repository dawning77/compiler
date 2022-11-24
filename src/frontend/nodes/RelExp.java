package frontend.nodes;

import frontend.token.TokenType;

import java.util.ArrayList;

public class RelExp {
    public  ArrayList<AddExp> addExps;
    public ArrayList<TokenType> ops;

    public RelExp(ArrayList<AddExp> addExps, ArrayList<TokenType> ops) {
        this.addExps = addExps;
        this.ops = ops;
    }
}
