package middle.operand.symbol;

public class Arr extends Symbol{
	public int len;

	public Arr(String name, Type type, int len){
		super(name, type, len);
		this.len = len;
	}
}
