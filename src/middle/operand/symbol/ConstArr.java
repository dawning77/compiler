package middle.operand.symbol;

import java.util.ArrayList;

public class ConstArr extends Arr implements Const{
    public ArrayList<Integer> vals;

    public ConstArr(String name, Type type, int len, ArrayList<Integer> vals){
        super(name, type, len);
        this.vals = vals;
    }
}
