package frontend.nodes;

import java.util.ArrayList;

public class LOrExp implements Cond{
    public ArrayList<LAndExp> lAndExps;

    public LOrExp(ArrayList<LAndExp> lAndExps) {
        this.lAndExps = lAndExps;
    }
}
