package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class And extends RType{
	public And(Reg rs, Reg rt, Reg rd){
		super(rs, rt, rd);
	}
}
