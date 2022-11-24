package frontend.nodes;

import java.util.ArrayList;

public class VarDecl implements Decl{
    public ArrayList<VarDef> varDefs;

    public VarDecl(ArrayList<VarDef> varDefs) {
        this.varDefs = varDefs;
    }
}
