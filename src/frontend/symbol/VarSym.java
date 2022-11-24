package frontend.symbol;

public class VarSym extends Symbol {
    public int dim;

    public VarSym(String name, String type, int line, int dim) {
        super(name, type, line);
        this.dim = dim;
    }
}
