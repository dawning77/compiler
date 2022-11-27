package middle.operand.symbol;

public class Mat extends Symbol{
	public int innerLen;
	public int outerLen;

	public Mat(String name, Type type, int innerLen, int outerLen){
		super(name, type, innerLen * outerLen);
		this.innerLen = innerLen;
		this.outerLen = outerLen;
	}
}
