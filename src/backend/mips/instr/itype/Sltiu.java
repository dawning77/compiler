package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Sltiu extends IType{
	public Sltiu(Reg rs, Reg rt, Integer imm){ super(rs, rt, imm); }
}