package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Mul extends RType{
	public Mul(Reg rs, Reg rt, Reg rd){
		super(rs, rt, rd);
	}
}
