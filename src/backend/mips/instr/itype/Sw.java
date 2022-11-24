package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Sw extends IType{
	public Sw(Reg rs, Reg rt, Integer imm){
		super(rs, rt, imm);
	}

	@Override
	public String toString(){ return "sw " + rt + ", " + imm + "(" + rs + ")"; }
}
