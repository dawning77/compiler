package frontend.nodes;

import java.util.ArrayList;

public class InitVal {
    public ArrayList<InitVal> initVals;
    public Exp exp;

    public InitVal(ArrayList<InitVal> initVals, Exp exp) {
        this.initVals = initVals;
        this.exp = exp;
    }
}
