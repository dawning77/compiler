package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Addi extends IType{
	public Addi(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}
}
