package middle.operand;

import java.util.*;

public class Imm implements Operand{
	public int val;

	public Imm(int val){
		this.val = val;
	}

	@Override
	public String toString(){ return String.valueOf(val); }

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Imm imm = (Imm)o;
		return val == imm.val;
	}

	@Override
	public int hashCode(){ return Objects.hash(val); }
}
