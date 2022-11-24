package frontend.nodes;

import frontend.token.TokenType;

import java.util.ArrayList;

public class AddExp implements Exp,ConstExp{
    public ArrayList<MulExp> mulExps ;
    public ArrayList<TokenType> ops;
    public int dim;

    public AddExp(ArrayList<MulExp> mulExps, ArrayList<TokenType> ops) {
        this.mulExps = mulExps;
        this.ops = ops;
        this.dim = mulExps.get(0).dim;
    }
}
