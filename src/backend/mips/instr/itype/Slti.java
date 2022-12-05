package backend.mips.instr.itype;

import backend.mips.reg.*;

public class Slti extends IType{
	public Slti(Reg rs, Reg rt, Integer imm){ super(rs, rt, imm); }
}
