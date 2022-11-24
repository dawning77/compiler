package frontend.nodes;

import frontend.token.Token;
import java.util.ArrayList;

public class VarDef implements Def{
    public Token ident;
    public ArrayList<ConstExp> constExps;
    public InitVal initVal;

    public VarDef(Token ident, ArrayList<ConstExp> constExps, InitVal initVal) {
        this.ident = ident;
        this.constExps = constExps;
        this.initVal = initVal;
    }
}
