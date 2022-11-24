package frontend.nodes;

import java.util.ArrayList;

public class ConstInitVal {
    public ArrayList<ConstInitVal> constInitVals;
    public ConstExp constExp;

    public ConstInitVal(ArrayList<ConstInitVal> constInitVals, ConstExp constExp) {
        this.constInitVals = constInitVals;
        this.constExp = constExp;
    }
}
