package middle.operand.symbol;

import middle.operand.Operand;

import java.util.*;

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

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol)o;
		return name.equals(symbol.name);
	}

	@Override
	public int hashCode(){ return Objects.hash(name); }
}
