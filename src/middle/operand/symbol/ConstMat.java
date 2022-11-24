package middle.operand.symbol;

import java.util.ArrayList;

public class ConstMat extends Mat implements Const{
    public ArrayList<ArrayList<Integer>> vals;

    public ConstMat(String name, Type type, int innerLen, int outerLen, ArrayList<ArrayList<Integer>> vals){
        super(name, type, innerLen, outerLen);
        this.vals = vals;
    }
}
