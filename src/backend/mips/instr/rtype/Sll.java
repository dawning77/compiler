package backend.mips.instr.rtype;

import backend.mips.reg.*;

public class Sll extends RType{
	public int imm;

	public Sll(Reg rt, Reg rd, int imm){
		super(null, rt, rd);
		this.imm = imm;
	}

	@Override
	public String toString(){ return "sll " + rd + ", " + rt + ", " + imm; }
}
