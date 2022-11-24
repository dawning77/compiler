package frontend.nodes;

import frontend.token.Token;
import java.util.ArrayList;

public class ConstDef implements Def{
    public Token ident;
    public ArrayList<ConstExp> constExps;
    public ConstInitVal constInitVal;

    public ConstDef(Token ident, ArrayList<ConstExp> constExps, ConstInitVal constInitVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.constInitVal = constInitVal;
    }
}
