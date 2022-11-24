package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Jr extends RType{
	public Jr(Reg rs){
		super(rs, Reg.$zero, Reg.$zero);
	}

	@Override
	public String toString(){ return "jr " + rs; }
}
