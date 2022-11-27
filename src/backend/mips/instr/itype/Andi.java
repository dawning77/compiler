package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Andi extends IType{
	public Andi(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}
}
