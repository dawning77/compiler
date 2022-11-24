package middle.operand.symbol;

public class ConstVar extends Var implements Const{
    public int val;

    public ConstVar(String name, Type type, int val){
        super(name, type);
        this.val = val;
    }
}
