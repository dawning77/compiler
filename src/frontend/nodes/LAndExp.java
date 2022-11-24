package frontend.nodes;

import java.util.ArrayList;

public class LAndExp {
    public ArrayList<EqExp> eqExps;

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }
}
