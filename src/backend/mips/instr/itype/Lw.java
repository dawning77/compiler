package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Lw extends IType{
	public Lw(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}

	@Override
	public String toString(){ return "lw " + rt + ", " + imm + "(" + rs + ")"; }
}
