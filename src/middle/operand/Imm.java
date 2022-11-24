package middle.operand;

public class Imm implements Operand{
	public int val;

	public Imm(int val){
		this.val = val;
	}

	@Override
	public String toString(){ return String.valueOf(val); }
}
