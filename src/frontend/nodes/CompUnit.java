package frontend.nodes;

import java.util.ArrayList;

public class CompUnit {
    public ArrayList<Decl> decls;
    public ArrayList<FuncDef> funcDefs;
    public MainFuncDef mainFuncDef;

    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }
}
