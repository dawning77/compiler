package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Add extends RType{
	public Add(Reg rs, Reg rt, Reg rd){
		super(rs, rt, rd);
	}

	@Override
	public String toString(){ return "addu " + rd + ", " + rs + ", " + rt; }
}
