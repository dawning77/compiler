package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Mul extends IType{
	public Mul(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}
}
