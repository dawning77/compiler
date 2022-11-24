package frontend.symbol;

import frontend.nodes.FuncFParams;
import frontend.token.TokenType;

import java.util.ArrayList;

public class FuncSym extends Symbol {
    public ArrayList<VarSym> varSyms;
    public String retType;

    public FuncSym(String name, String type, int line, TokenType funcType, FuncFParams params) {
        super(name, type, line);
        this.varSyms = params == null ? new ArrayList<>() : params.varSyms;
        this.retType = funcType.equals(TokenType.INTTK) ? "int" : "void";
    }
}
