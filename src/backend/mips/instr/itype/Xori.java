package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Xori extends IType{
	public Xori(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}
}
