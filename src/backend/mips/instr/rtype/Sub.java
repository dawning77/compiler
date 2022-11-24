package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Sub extends RType{
	public Sub(Reg rs, Reg rt, Reg rd){
		super(rs, rt, rd);
	}

	@Override
	public String toString(){ return "subu " + rd + ", " + rs + ", " + rt; }
}
