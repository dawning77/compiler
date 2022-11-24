package frontend.nodes;

import frontend.symbol.VarSym;

import java.util.ArrayList;

public class FuncFParams {
    public ArrayList<FuncFParam> funcFParams;
    public ArrayList<VarSym> varSyms;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
        this.varSyms = new ArrayList<>();
        for (FuncFParam funcFParam : funcFParams) {
            varSyms.add(funcFParam.varSym);
        }
    }
}
