package frontend.nodes;

import java.util.ArrayList;

public class ConstDecl implements Decl{
    public ArrayList<ConstDef> constDefs;

    public ConstDecl(ArrayList<ConstDef> constDefs) {
        this.constDefs = constDefs;
    }
}
