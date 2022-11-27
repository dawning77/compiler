package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Sra extends RType{
	public int imm;

	public Sra(Reg rt, Reg rd, int imm){
		super(null, rt, rd);
		this.imm = imm;
	}

	@Override
	public String toString(){ return "sra " + rd + ", " + rt + ", " + imm; }
}
