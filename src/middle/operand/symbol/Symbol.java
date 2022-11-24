package middle.operand.symbol;

import middle.operand.Operand;

public abstract class Symbol implements Operand{
	public final String name;
	public final Type type;
	public final int size;
	public int loc;     // offset from the base

	public enum Type{
		param, tmp, local, global
	}

	public Symbol(String name, Type type, int size){
		this.name = name;
		this.type = type;
		this.size = size;
		this.loc = -1;
	}

	@Override
	public String toString(){ return name; }
}
